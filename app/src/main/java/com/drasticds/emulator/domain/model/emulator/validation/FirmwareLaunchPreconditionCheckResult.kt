package com.drasticds.emulator.domain.model.emulator.validation

import com.drasticds.emulator.domain.model.ConfigurationDirResult
import com.drasticds.emulator.domain.model.ConsoleType

sealed class FirmwareLaunchPreconditionCheckResult {
    data class Success(val consoleType: ConsoleType) : FirmwareLaunchPreconditionCheckResult()
    data class BiosConfigurationIncorrect(val configurationDirectoryResult: ConfigurationDirResult) : FirmwareLaunchPreconditionCheckResult()
}