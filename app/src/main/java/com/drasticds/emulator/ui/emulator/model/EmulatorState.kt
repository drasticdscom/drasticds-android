package com.drasticds.emulator.ui.emulator.model

import com.drasticds.emulator.MelonEmulator
import com.drasticds.emulator.domain.model.ConsoleType
import com.drasticds.emulator.domain.model.rom.Rom

sealed class EmulatorState {
    data object Uninitialized : EmulatorState()
    data object LoadingRom : EmulatorState()
    data object LoadingFirmware : EmulatorState()
    data class RunningRom(val rom: Rom) : EmulatorState()
    data class RunningFirmware(val console: ConsoleType) : EmulatorState()
    data object RomLoadError : EmulatorState()
    data class FirmwareLoadError(val reason: MelonEmulator.FirmwareLoadResult) : EmulatorState()
    data class RomNotFoundError(val romPath: String) : EmulatorState()

    fun isRunning() = this is RunningRom || this is RunningFirmware
}