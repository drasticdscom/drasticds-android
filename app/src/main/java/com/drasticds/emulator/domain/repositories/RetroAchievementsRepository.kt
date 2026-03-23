package com.drasticds.emulator.domain.repositories

import com.drasticds.emulator.domain.model.retroachievements.RAAchievementSetSummary
import com.drasticds.emulator.domain.model.retroachievements.RAGameSummary
import com.drasticds.emulator.domain.model.retroachievements.RARuntimeUserAchievement
import com.drasticds.emulator.domain.model.retroachievements.RAUserAchievement
import com.drasticds.emulator.domain.model.retroachievements.RAUserGameData
import com.drasticds.emulator.rcheevosapi.model.RAAchievement
import com.drasticds.emulator.rcheevosapi.model.RAAwardAchievementResponse
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import com.drasticds.emulator.rcheevosapi.model.RALeaderboard
import com.drasticds.emulator.rcheevosapi.model.RASetId
import com.drasticds.emulator.rcheevosapi.model.RASubmitLeaderboardEntryResponse
import com.drasticds.emulator.rcheevosapi.model.RAUserAuth

interface RetroAchievementsRepository {
    suspend fun isUserAuthenticated(): Boolean
    suspend fun getUserAuthentication(): RAUserAuth?
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun getUserGameData(gameHash: String, forHardcoreMode: Boolean): Result<RAUserGameData?>
    suspend fun getRuntimeUserAchievements(achievements: List<RAUserAchievement>): List<RARuntimeUserAchievement>
    suspend fun getGameSummary(gameHash: String): RAGameSummary?
    suspend fun getGameSummary(gameId: RAGameId): RAGameSummary?
    suspend fun getAchievementSetSummary(setId: RASetId): RAAchievementSetSummary?
    suspend fun getAchievement(achievementId: Long): Result<RAAchievement?>
    suspend fun awardAchievement(achievement: RAAchievement, forHardcoreMode: Boolean): Result<RAAwardAchievementResponse>
    suspend fun submitPendingAchievements(): Result<Unit>
    suspend fun getLeaderboard(leaderboardId: Long): RALeaderboard?
    suspend fun submitLeaderboardEntry(leaderboardId: Long, value: Int): Result<RASubmitLeaderboardEntryResponse>
    suspend fun startSession(gameHash: String): Result<Unit>
    suspend fun sendSessionHeartbeat(gameHash: String, richPresenceDescription: String?)
}