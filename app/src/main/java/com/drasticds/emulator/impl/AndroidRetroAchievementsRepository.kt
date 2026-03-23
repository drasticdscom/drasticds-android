package com.drasticds.emulator.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.drasticds.emulator.MelonEmulator
import com.drasticds.emulator.common.suspendMapCatching
import com.drasticds.emulator.common.suspendRecoverCatching
import com.drasticds.emulator.common.suspendRunCatching
import com.drasticds.emulator.common.workers.RetroAchievementsSubmissionWorker
import com.drasticds.emulator.database.daos.RetroAchievementsDao
import com.drasticds.emulator.database.entities.retroachievements.RAGameEntity
import com.drasticds.emulator.database.entities.retroachievements.RAGameHashEntity
import com.drasticds.emulator.database.entities.retroachievements.RAGameSetMetadata
import com.drasticds.emulator.database.entities.retroachievements.RAPendingAchievementSubmissionEntity
import com.drasticds.emulator.database.entities.retroachievements.RAUserAchievementEntity
import com.drasticds.emulator.domain.model.retroachievements.RAAchievementSetSummary
import com.drasticds.emulator.domain.model.retroachievements.RAGameSummary
import com.drasticds.emulator.domain.model.retroachievements.RARuntimeUserAchievement
import com.drasticds.emulator.domain.model.retroachievements.RAUserAchievement
import com.drasticds.emulator.domain.model.retroachievements.RAUserAchievementSet
import com.drasticds.emulator.domain.model.retroachievements.RAUserGameData
import com.drasticds.emulator.domain.model.retroachievements.exception.RAGameNotExist
import com.drasticds.emulator.domain.repositories.RetroAchievementsRepository
import com.drasticds.emulator.impl.mappers.retroachievements.mapToEntity
import com.drasticds.emulator.impl.mappers.retroachievements.mapToModel
import com.drasticds.emulator.utils.enumValueOfIgnoreCase
import com.drasticds.emulator.rcheevosapi.RAApi
import com.drasticds.emulator.rcheevosapi.RAUserAuthStore
import com.drasticds.emulator.rcheevosapi.model.RAAchievement
import com.drasticds.emulator.rcheevosapi.model.RAAwardAchievementResponse
import com.drasticds.emulator.rcheevosapi.model.RAGame
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import com.drasticds.emulator.rcheevosapi.model.RALeaderboard
import com.drasticds.emulator.rcheevosapi.model.RASetId
import com.drasticds.emulator.rcheevosapi.model.RASubmitLeaderboardEntryResponse
import com.drasticds.emulator.rcheevosapi.model.RAUserAuth
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class AndroidRetroAchievementsRepository(
    private val raApi: RAApi,
    private val retroAchievementsDao: RetroAchievementsDao,
    private val raUserAuthStore: RAUserAuthStore,
    private val sharedPreferences: SharedPreferences,
    private val context: Context,
) : RetroAchievementsRepository {

    private companion object {
        const val RA_HASH_LIBRARY_LAST_UPDATED = "ra_hash_library_last_updated"
        const val PENDING_ACHIEVEMENT_SUBMISSION_WORKER_NAME = "ra_pending_achievement_submission_worker"
    }

    override suspend fun isUserAuthenticated(): Boolean {
        return raUserAuthStore.getUserAuth() != null
    }

    override suspend fun getUserAuthentication(): RAUserAuth? {
        return raUserAuthStore.getUserAuth()
    }

    override suspend fun login(username: String, password: String): Result<Unit> {
        return raApi.login(username, password)
    }

    override suspend fun logout() {
        retroAchievementsDao.deleteAllAchievementUserData()
        raUserAuthStore.clearUserAuth()
    }

    override suspend fun getUserGameData(gameHash: String, forHardcoreMode: Boolean): Result<RAUserGameData?> {
        val gameIdResult = getGameIdFromGameHash(gameHash)
        if (gameIdResult.isFailure) {
            return Result.failure(gameIdResult.exceptionOrNull()!!)
        }

        val gameId = gameIdResult.getOrThrow()
        if (gameId == null) {
            return Result.success(null)
        }

        val gameSetMetadata = retroAchievementsDao.getGameSetMetadata(gameId.id)
        val currentMetadata = CurrentGameSetMetadata(gameId, gameSetMetadata)

        val gameDataResult = fetchGameData(gameId, gameHash, currentMetadata)
        if (gameDataResult.isFailure) {
            return Result.failure(gameDataResult.exceptionOrNull()!!)
        }

        val userUnlocksResult = fetchGameUserUnlockedAchievements(gameId, forHardcoreMode, currentMetadata)
        if (userUnlocksResult.isFailure) {
            return Result.failure(userUnlocksResult.exceptionOrNull()!!)
        }

        val gameData = gameDataResult.getOrThrow()
        val userUnlocks = userUnlocksResult.getOrThrow()

        val userGameData = gameData?.let {
            val userSets = it.sets.map {
                RAUserAchievementSet(
                    id = it.id,
                    gameId = it.gameId,
                    title = it.title,
                    type = it.type,
                    iconUrl = it.iconUrl,
                    achievements = it.achievements
                        .filter { it.type == RAAchievement.Type.CORE }
                        .map { achievement ->
                            RAUserAchievement(
                                achievement = achievement,
                                isUnlocked = userUnlocks.contains(achievement.id),
                                forHardcoreMode = forHardcoreMode,
                            )
                        },
                    leaderboards = it.leaderboards.filter { !it.hidden },
                )
            }

            RAUserGameData(
                id = it.id,
                title = it.title,
                icon = it.icon,
                richPresencePatch = it.richPresencePatch,
                sets = userSets,
            )
        }
        return Result.success(userGameData)
    }

    override suspend fun getRuntimeUserAchievements(achievements: List<RAUserAchievement>): List<RARuntimeUserAchievement> = withContext(Dispatchers.Default) {
        val runtimeAchievements = MelonEmulator.getRuntimeAchievements()
        achievements.map { userAchievement ->
            val runtimeAchievement = runtimeAchievements.firstOrNull { it.id == userAchievement.achievement.id }
            RARuntimeUserAchievement(
                userAchievement = userAchievement,
                progress = runtimeAchievement?.value ?: 0,
                target = runtimeAchievement?.target ?: 0,
            )
        }
    }

    override suspend fun getGameSummary(gameHash: String): RAGameSummary? {
        val gameId = getGameIdFromGameHash(gameHash).getOrNull() ?: return null
        return getGameSummary(gameId)
    }

    override suspend fun getGameSummary(gameId: RAGameId): RAGameSummary? {
        return retroAchievementsDao.getGame(gameId.id)?.let {
            RAGameSummary(
                title = it.title,
                icon = URL(it.icon),
                richPresencePatch = it.richPresencePatch,
            )
        }
    }

    override suspend fun getAchievementSetSummary(setId: RASetId): RAAchievementSetSummary? {
        return retroAchievementsDao.getAchievementSet(setId.id)?.let {
            RAAchievementSetSummary(
                setId = it.id,
                gameId = RAGameId(it.gameId),
                title = it.title,
                type = enumValueOfIgnoreCase(it.type),
                iconUrl = URL(it.iconUrl),
            )
        }
    }

    override suspend fun getAchievement(achievementId: Long): Result<RAAchievement?> {
        return suspendRunCatching {
            retroAchievementsDao.getAchievement(achievementId)
        }.map {
            it?.mapToModel()
        }
    }

    override suspend fun awardAchievement(achievement: RAAchievement, forHardcoreMode: Boolean): Result<RAAwardAchievementResponse> {
        return submitAchievementAward(achievement.id, achievement.gameId, forHardcoreMode).onFailure {
            scheduleAchievementSubmissionJob()
        }
    }

    override suspend fun submitPendingAchievements(): Result<Unit> {
        retroAchievementsDao.getPendingAchievementSubmissions().forEach {
            // Do not schedule resubmission if this fails. The current submission job should schedule another attempt
            val submissionResult = submitAchievementAward(it.achievementId, RAGameId(it.gameId), it.forHardcoreMode)
            if (submissionResult.isFailure) {
                return submissionResult.map { }
            }

            retroAchievementsDao.removePendingAchievementSubmission(it)
        }

        return Result.success(Unit)
    }

    override suspend fun getLeaderboard(leaderboardId: Long): RALeaderboard? {
        return retroAchievementsDao.getLeaderboard(leaderboardId)?.mapToModel()
    }

    override suspend fun submitLeaderboardEntry(leaderboardId: Long, value: Int): Result<RASubmitLeaderboardEntryResponse> {
        return raApi.submitLeaderboardEntry(leaderboardId, value)
    }

    override suspend fun startSession(gameHash: String): Result<Unit> {
        val gameId = getGameIdFromGameHash(gameHash).getOrNull() ?: return Result.failure(RAGameNotExist(gameHash))
        return raApi.startSession(gameId)
    }

    override suspend fun sendSessionHeartbeat(gameHash: String, richPresenceDescription: String?) {
        val gameId = getGameIdFromGameHash(gameHash).getOrNull() ?: return
        raApi.sendPing(gameId, richPresenceDescription)
    }

    private suspend fun submitAchievementAward(achievementId: Long, gameId: RAGameId, forHardcoreMode: Boolean): Result<RAAwardAchievementResponse> {
        // Award the achievement immediately locally
        val userAchievement = RAUserAchievementEntity(
            gameId = gameId.id,
            achievementId = achievementId,
            isUnlocked = true,
            isHardcore = forHardcoreMode,
        )
        retroAchievementsDao.addUserAchievement(userAchievement)

        return raApi.awardAchievement(achievementId, forHardcoreMode).onFailure {
            // On failure, insert it into the pending achievements to be re-submitted later
            val pendingAchievementSubmissionEntity = RAPendingAchievementSubmissionEntity(
                achievementId = achievementId,
                gameId = gameId.id,
                forHardcoreMode = forHardcoreMode,
            )
            retroAchievementsDao.addPendingAchievementSubmission(pendingAchievementSubmissionEntity)
        }
    }

    private suspend fun getGameIdFromGameHash(gameHash: String): Result<RAGameId?> {
        return if (mustRefreshHashLibrary()) {
            raApi.getGameHashList()
                .onSuccess { gameHashes ->
                    val gameHashEntities = gameHashes.map {
                        RAGameHashEntity(it.key, it.value.id)
                    }
                    retroAchievementsDao.updateGameHashLibrary(gameHashEntities)
                    sharedPreferences.edit {
                        putLong(RA_HASH_LIBRARY_LAST_UPDATED, Clock.System.now().toEpochMilliseconds())
                    }
                }
                .map {
                    it[gameHash]
                }
                .suspendRecoverCatching {
                    retroAchievementsDao.getGameHashEntity(gameHash)?.let {
                        RAGameId(it.gameId)
                    }
                }
        } else {
            suspendRunCatching {
                retroAchievementsDao.getGameHashEntity(gameHash)?.let {
                    RAGameId(it.gameId)
                }
            }
        }
    }

    private suspend fun fetchGameData(gameId: RAGameId, gameHash: String, gameSetMetadata: CurrentGameSetMetadata): Result<RAGame?> {
        return if (mustRefreshAchievementSet(gameSetMetadata.currentMetadata)) {
            raApi.getGameAchievementSets(gameHash).suspendMapCatching { game ->
                val sets = game.sets.map {
                    it.mapToEntity()
                }
                val achievementEntities = game.sets.flatMap { set ->
                    set.achievements.map { it.mapToEntity() }
                }
                val leaderboardEntities = game.sets.flatMap { set ->
                    set.leaderboards.map { it.mapToEntity() }
                }

                val gameEntity = RAGameEntity(game.id.id, game.richPresencePatch, game.title, game.icon.toString())
                val newMetadata = gameSetMetadata.withNewAchievementSetUpdate()
                retroAchievementsDao.updateGameData(gameEntity, sets, achievementEntities, leaderboardEntities)
                retroAchievementsDao.updateGameSetMetadata(newMetadata)
                game
            }.suspendRecoverCatching { exception ->
                if (gameSetMetadata.isGameAchievementDataKnown()) {
                    // Load DB data because we know that it was previously loaded
                    retroAchievementsDao.getGameWithSets(gameId.id)?.mapToModel()
                } else {
                    // The achievement data has never been downloaded for this game. Rethrow exception
                    throw exception
                }
            }
        } else {
            suspendRunCatching {
                retroAchievementsDao.getGameWithSets(gameId.id)?.mapToModel()
            }
        }
    }

    private suspend fun fetchGameUserUnlockedAchievements(gameId: RAGameId, forHardcoreMode: Boolean, gameSetMetadata: CurrentGameSetMetadata): Result<List<Long>> {
        return if (mustRefreshUserData(gameSetMetadata.currentMetadata, forHardcoreMode)) {
            raApi.getUserUnlockedAchievements(gameId, forHardcoreMode).onSuccess { userUnlocks ->
                val userAchievementEntities = userUnlocks.map {
                    RAUserAchievementEntity(
                        gameId = gameId.id,
                        achievementId = it,
                        isUnlocked = true,
                        isHardcore = forHardcoreMode,
                    )
                }

                val newMetadata = gameSetMetadata.withNewUserAchievementsUpdate(forHardcoreMode)
                retroAchievementsDao.updateGameUserUnlockedAchievements(gameId.id, userAchievementEntities)
                retroAchievementsDao.updateGameSetMetadata(newMetadata)
            }.suspendRecoverCatching { exception ->
                if (gameSetMetadata.isUserAchievementDataKnown(forHardcoreMode)) {
                    // Load DB data because we know that it was previously loaded
                    retroAchievementsDao.getGameUserUnlockedAchievements(gameId.id, forHardcoreMode).map {
                        it.achievementId
                    }
                } else {
                    // The user's achievement data has never been downloaded for this game. Rethrow exception
                    throw exception
                }
            }
        } else {
            suspendRunCatching {
                retroAchievementsDao.getGameUserUnlockedAchievements(gameId.id, forHardcoreMode).map {
                    it.achievementId
                }
            }
        }
    }

    private fun mustRefreshHashLibrary(): Boolean {
        val hashLibraryLastUpdateTimestamp = sharedPreferences.getLong(RA_HASH_LIBRARY_LAST_UPDATED, 0)
        val hashLibraryLastUpdate = Instant.fromEpochMilliseconds(hashLibraryLastUpdateTimestamp)

        // Update the game hash library once a day
        return (Clock.System.now() - hashLibraryLastUpdate) > 1.days
    }

    private fun mustRefreshAchievementSet(gameSetMetadata: RAGameSetMetadata?): Boolean {
        if (gameSetMetadata?.lastAchievementSetUpdated == null) {
            return true
        }

        // Update the achievement set once a week
        return (Clock.System.now() - gameSetMetadata.lastAchievementSetUpdated) >= 7.days
    }

    private fun mustRefreshUserData(gameSetMetadata: RAGameSetMetadata?, forHardcoreMode: Boolean): Boolean {
        val lastUserDataUpdateTimestamp = if (forHardcoreMode) {
            gameSetMetadata?.lastHardcoreUserDataUpdated
        } else {
            gameSetMetadata?.lastSoftcoreUserDataUpdated
        }

        if (lastUserDataUpdateTimestamp == null) {
            return true
        }

        // Sync user achievement data once a day
        return (Clock.System.now() - lastUserDataUpdateTimestamp) >= 1.days
    }

    private fun scheduleAchievementSubmissionJob() {
        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<RetroAchievementsSubmissionWorker>()
            .setConstraints(workConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 60, TimeUnit.SECONDS)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(PENDING_ACHIEVEMENT_SUBMISSION_WORKER_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, workRequest)
    }

    private class CurrentGameSetMetadata(private val gameId: RAGameId, initialMetadata: RAGameSetMetadata?) {
        var currentMetadata = initialMetadata
            private set

        fun withNewAchievementSetUpdate(): RAGameSetMetadata {
            return (currentMetadata?.copy(lastAchievementSetUpdated = Clock.System.now()) ?: RAGameSetMetadata(gameId.id, Clock.System.now(), null, null)).also {
                currentMetadata = it
            }
        }

        fun withNewUserAchievementsUpdate(forHardcoreMode: Boolean): RAGameSetMetadata {
            return if (forHardcoreMode) {
                currentMetadata?.copy(lastHardcoreUserDataUpdated = Clock.System.now()) ?: RAGameSetMetadata(gameId.id, null, null, Clock.System.now()).also {
                    currentMetadata = it
                }
            } else {
                currentMetadata?.copy(lastSoftcoreUserDataUpdated = Clock.System.now()) ?: RAGameSetMetadata(gameId.id, null, Clock.System.now(), null).also {
                    currentMetadata = it
                }
            }
        }

        fun isGameAchievementDataKnown(): Boolean {
            return currentMetadata?.lastAchievementSetUpdated != null
        }

        fun isUserAchievementDataKnown(forHardcoreMode: Boolean): Boolean {
            return if (forHardcoreMode) {
                currentMetadata?.lastHardcoreUserDataUpdated != null
            } else {
                currentMetadata?.lastSoftcoreUserDataUpdated != null
            }
        }
    }
}