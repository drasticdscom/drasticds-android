package com.drasticds.emulator.ui.settings.model

sealed class RetroAchievementsAccountState {
    object Unknown : RetroAchievementsAccountState()
    object LoggedOut : RetroAchievementsAccountState()
    data class LoggedIn(val accountName: String) : RetroAchievementsAccountState()
}