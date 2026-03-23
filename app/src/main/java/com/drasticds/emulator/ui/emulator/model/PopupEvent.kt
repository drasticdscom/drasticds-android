package com.drasticds.emulator.ui.emulator.model

import com.drasticds.emulator.rcheevosapi.model.RAAchievement

sealed class PopupEvent {
    data class AchievementUnlockPopup(val achievement: RAAchievement) : PopupEvent()
    data class GameMasteredPopup(val event: RAEventUi.GameMastered) : PopupEvent()
    data class RAIntegrationPopup(val event: RAIntegrationEvent) : PopupEvent()
}