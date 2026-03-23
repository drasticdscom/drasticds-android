package com.drasticds.emulator.ui.romdetails

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import com.drasticds.emulator.domain.model.retroachievements.RAUserAchievement
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.repositories.RetroAchievementsRepository
import com.drasticds.emulator.domain.repositories.SettingsRepository
import com.drasticds.emulator.parcelables.RomParcelable
import com.drasticds.emulator.ui.common.achievements.ui.model.AchievementUiModel
import com.drasticds.emulator.ui.common.achievements.viewmodel.RetroAchievementsViewModel
import com.drasticds.emulator.ui.romdetails.model.AchievementBucketUiModel
import javax.inject.Inject

@HiltViewModel
class RomDetailsRetroAchievementsViewModel @Inject constructor(
    retroAchievementsRepository: RetroAchievementsRepository,
    settingsRepository: SettingsRepository,
    private val savedStateHandle: SavedStateHandle,
) : RetroAchievementsViewModel(retroAchievementsRepository, settingsRepository) {

    override fun getRom(): Rom {
        return savedStateHandle.get<RomParcelable>(RomDetailsActivity.KEY_ROM)!!.rom
    }

    override suspend fun buildAchievementBuckets(achievements: List<RAUserAchievement>): List<AchievementBucketUiModel> {
        return achievements.groupingBy {
            if (it.isUnlocked) {
                AchievementBucketUiModel.Bucket.Unlocked
            } else {
                AchievementBucketUiModel.Bucket.Locked
            }
        }.aggregate { _, accumulator: MutableList<AchievementUiModel>?, element, _ ->
            val achievementUiModel = AchievementUiModel.UserAchievementUiModel(element)
            accumulator?.apply {
                add(achievementUiModel)
            } ?: mutableListOf(achievementUiModel)
        }.map {
            AchievementBucketUiModel(it.key, it.value)
        }.sortedBy {
            // Display unlocked achievements first
            if (it.bucket == AchievementBucketUiModel.Bucket.Unlocked) 0 else 1
        }
    }
}