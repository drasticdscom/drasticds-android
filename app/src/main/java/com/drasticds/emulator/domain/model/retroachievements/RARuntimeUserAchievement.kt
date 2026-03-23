package com.drasticds.emulator.domain.model.retroachievements

data class RARuntimeUserAchievement(
    val userAchievement: RAUserAchievement,
    val progress: Int,
    val target: Int,
) {

    fun hasProgress() = progress > 0

    fun relativeProgress() = if (target == 0) 0f else progress.toFloat() / target
}