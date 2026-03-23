package com.drasticds.emulator.ui.emulator.model

import com.drasticds.emulator.domain.model.RomInfo
import com.drasticds.emulator.domain.model.SaveStateSlot
import com.drasticds.emulator.ui.emulator.rewind.model.RewindWindow

sealed class EmulatorUiEvent {
    sealed class OpenScreen : EmulatorUiEvent() {
        data object SettingsScreen : OpenScreen()
        data class CheatsScreen(val romInfo: RomInfo) : OpenScreen()
    }
    data class ShowPauseMenu(val pauseMenu: PauseMenu) : EmulatorUiEvent()
    data class ShowRewindWindow(val rewindWindow: RewindWindow) : EmulatorUiEvent()
    data class ShowRomSaveStates(val saveStates: List<SaveStateSlot>, val reason: Reason) : EmulatorUiEvent() {
        enum class Reason {
            SAVING,
            LOADING,
        }
    }
    data object ShowAchievementList : EmulatorUiEvent()
    data object CloseEmulator : EmulatorUiEvent()
}