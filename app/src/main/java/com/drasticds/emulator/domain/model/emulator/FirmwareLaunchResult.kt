package com.drasticds.emulator.domain.model.emulator

import com.drasticds.emulator.MelonEmulator

sealed class FirmwareLaunchResult {
    data class LaunchFailed(val reason: MelonEmulator.FirmwareLoadResult) : FirmwareLaunchResult()
    object LaunchSuccessful : FirmwareLaunchResult()
}