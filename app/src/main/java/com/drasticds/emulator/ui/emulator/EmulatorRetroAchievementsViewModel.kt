package com.drasticds.emulator.ui.emulator

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.drasticds.emulator.domain.model.retroachievements.RAEvent
import com.drasticds.emulator.domain.model.retroachievements.RAUserAchievement
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.repositories.RetroAchievementsRepository
import com.drasticds.emulator.domain.repositories.SettingsRepository
import com.drasticds.emulator.domain.services.EmulatorManager
import com.drasticds.emulator.impl.emulator.EmulatorSession
import com.drasticds.emulator.ui.common.achievements.ui.model.AchievementUiModel
import com.drasticds.emulator.ui.common.achievements.viewmodel.RetroAchievementsViewModel
import com.drasticds.emulator.ui.romdetails.model.AchievementBucketUiModel
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@HiltViewModel
class EmulatorRetroAchievementsViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    private val retroAchievementsRepository: RetroAchievementsRepository,
    private val emulatorSession: EmulatorSession,
    private val emulatorManager: EmulatorManager,
) : RetroAchievementsViewModel(retroAchievementsRepository, settingsRepository) {

    private class TimedUnlockedAchievement(
        val achievementId: Long,
        val unlockedAt: Instant,
    )

    private var activeChallengesIds = emptyList<Long>()
    private var recentlyUnlockedAchievements = emptyList<TimedUnlockedAchievement>()

    init {
        viewModelScope.launch {
            emulatorManager.observeRetroAchievementEvents().collect { event ->
                when (event) {
                    is RAEvent.OnAchievementPrimed -> activeChallengesIds += event.achievementId
                    is RAEvent.OnAchievementUnPrimed -> activeChallengesIds -= event.achievementId
                    is RAEvent.OnAchievementTriggered -> recentlyUnlockedAchievements += TimedUnlockedAchievement(event.achievementId, Clock.System.now())
                    else -> { /* no-op */ }
                }
            }
        }
    }

    fun onSessionReset() {
        activeChallengesIds = emptyList()
    }

    override fun getRom(): Rom {
        val romSession = emulatorSession.currentSessionType() as? EmulatorSession.SessionType.RomSession
        if (romSession == null) {
            error("Emulator must be running a ROM session")
        }

        return romSession.rom
    }

    override suspend fun buildAchievementBuckets(achievements: List<RAUserAchievement>): List<AchievementBucketUiModel> {
        val now = Clock.System.now()
        return retroAchievementsRepository.getRuntimeUserAchievements(achievements).groupingBy { runtimeAchievement ->
            when {
                runtimeAchievement.userAchievement.isUnlocked -> {
                    val recentlyUnlockedAchievement = recentlyUnlockedAchievements.firstOrNull { it.achievementId == runtimeAchievement.userAchievement.achievement.id }
                    if (recentlyUnlockedAchievement != null && (now - recentlyUnlockedAchievement.unlockedAt) < 10.minutes) {
                        AchievementBucketUiModel.Bucket.RecentlyUnlocked
                    } else {
                        AchievementBucketUiModel.Bucket.Unlocked
                    }
                }
                activeChallengesIds.any { it == runtimeAchievement.userAchievement.achievement.id } -> AchievementBucketUiModel.Bucket.ActiveChallenges
                runtimeAchievement.relativeProgress() >= 0.8f -> AchievementBucketUiModel.Bucket.AlmostThere
                else -> AchievementBucketUiModel.Bucket.Locked
            }
        }.aggregate { _, accumulator: MutableList<AchievementUiModel>?, element, _ ->
            val achievementUiModel = AchievementUiModel.RuntimeAchievementUiModel(element)
            accumulator?.apply {
                add(achievementUiModel)
            } ?: mutableListOf(achievementUiModel)
        }.map {
            val achievements = if (it.key == AchievementBucketUiModel.Bucket.RecentlyUnlocked) {
                // Sort recently unlocked achievements
                it.value.sortedByDescending { achievement ->
                    recentlyUnlockedAchievements.firstOrNull { it.achievementId == achievement.actualAchievement().id }?.unlockedAt
                }
            } else {
                it.value
            }
            AchievementBucketUiModel(it.key, achievements)
        }.sortedBy {
            it.bucket.displayOrder
        }
    }
}