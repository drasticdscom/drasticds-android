package com.drasticds.emulator.ui.common.achievements.ui.model

import com.drasticds.emulator.domain.model.retroachievements.RARuntimeUserAchievement
import com.drasticds.emulator.domain.model.retroachievements.RAUserAchievement
import com.drasticds.emulator.rcheevosapi.model.RAAchievement

sealed class AchievementUiModel {

    data class PrimedAchievementUiModel(
        val achievement: RAAchievement,
    ) : AchievementUiModel()

    data class UserAchievementUiModel(
        val userAchievement: RAUserAchievement,
    ) : AchievementUiModel()

    data class RuntimeAchievementUiModel(
        val runtimeAchievement: RARuntimeUserAchievement,
    ) : AchievementUiModel() {

        fun hasProgress() = runtimeAchievement.hasProgress()
    }

    fun actualAchievement(): RAAchievement = when (this) {
        is PrimedAchievementUiModel -> achievement
        is UserAchievementUiModel -> userAchievement.achievement
        is RuntimeAchievementUiModel -> runtimeAchievement.userAchievement.achievement
    }
}