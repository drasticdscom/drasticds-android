package com.drasticds.emulator.ui.dsiwaremanager.model

import com.drasticds.emulator.domain.model.ConfigurationDirResult
import com.drasticds.emulator.domain.model.DSiWareTitle

sealed class DSiWareManagerUiState {
    data class DSiSetupInvalid(val status: ConfigurationDirResult.Status) : DSiWareManagerUiState()
    object Loading : DSiWareManagerUiState()
    data class Ready(val titles: List<DSiWareTitle>) : DSiWareManagerUiState()
    object Error : DSiWareManagerUiState()
}