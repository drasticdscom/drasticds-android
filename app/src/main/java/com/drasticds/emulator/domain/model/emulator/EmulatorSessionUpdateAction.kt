package com.drasticds.emulator.domain.model.emulator

sealed class EmulatorSessionUpdateAction {
    data object EnableRetroAchievements : EmulatorSessionUpdateAction()
    data object DisableRetroAchievements : EmulatorSessionUpdateAction()
    data object NotifyRetroAchievementsModeSwitch : EmulatorSessionUpdateAction()
}