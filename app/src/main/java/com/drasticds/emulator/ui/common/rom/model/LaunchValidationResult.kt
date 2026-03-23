package com.drasticds.emulator.ui.common.rom.model

import com.drasticds.emulator.domain.model.emulator.validation.FirmwareLaunchPreconditionCheckResult
import com.drasticds.emulator.domain.model.emulator.validation.RomLaunchPreconditionCheckResult

sealed class LaunchValidationResult {
    data class Rom(val result: RomLaunchPreconditionCheckResult) : LaunchValidationResult()
    data class Firmware(val result: FirmwareLaunchPreconditionCheckResult) : LaunchValidationResult()
}