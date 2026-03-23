package com.drasticds.emulator.ui.common.rom

import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.emulator.validation.RomLaunchPreconditionCheckResult

sealed class RomValidationResult {
    data class Success(val rom: Rom) : RomValidationResult()
    data class PreconditionsCheckFailed(val preconditionsCheckResult: RomLaunchPreconditionCheckResult) : RomValidationResult()
}