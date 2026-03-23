package com.drasticds.emulator.ui.dsiwaremanager.model

import com.drasticds.emulator.domain.model.rom.Rom

sealed class DSiWareMangerRomListUiState {
    object Loading : DSiWareMangerRomListUiState()
    class Loaded(val roms: List<Rom>) : DSiWareMangerRomListUiState()
    object Empty : DSiWareMangerRomListUiState()
}