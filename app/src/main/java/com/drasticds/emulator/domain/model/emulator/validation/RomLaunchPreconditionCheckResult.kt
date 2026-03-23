package com.drasticds.emulator.domain.model.emulator.validation

import com.drasticds.emulator.domain.model.ConfigurationDirResult
import com.drasticds.emulator.domain.model.rom.Rom

sealed class RomLaunchPreconditionCheckResult {
    data class Success(val rom: Rom) : RomLaunchPreconditionCheckResult()
    data class DSiWareTitleValidationFailed(val reason: Reason) : RomLaunchPreconditionCheckResult() {
        sealed class Reason {
            object NandError : Reason()
            object RomParseError : Reason()
            object TitleNotInstalled : Reason()
        }
    }
    data class BiosConfigurationIncorrect(val configurationDirectoryResult: ConfigurationDirResult) : RomLaunchPreconditionCheckResult()
}