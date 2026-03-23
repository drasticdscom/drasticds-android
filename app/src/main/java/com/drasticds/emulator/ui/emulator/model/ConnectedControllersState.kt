package com.drasticds.emulator.ui.emulator.model

import com.drasticds.emulator.domain.model.Input

sealed class ConnectedControllersState {
    data object NoControllers : ConnectedControllersState()
    data class ControllersConnected(val assignedInputs: List<Input>) : ConnectedControllersState()
}