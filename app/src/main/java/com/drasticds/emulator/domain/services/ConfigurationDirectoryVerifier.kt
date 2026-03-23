package com.drasticds.emulator.domain.services

import android.net.Uri
import com.drasticds.emulator.domain.model.ConfigurationDirResult
import com.drasticds.emulator.domain.model.ConsoleType
import com.drasticds.emulator.domain.repositories.SettingsRepository

abstract class ConfigurationDirectoryVerifier(val settingsRepository: SettingsRepository) {
    fun checkDsConfigurationDirectory(): ConfigurationDirResult {
        return checkDsConfigurationDirectory(settingsRepository.getDsBiosDirectory())
    }

    fun checkDsiConfigurationDirectory(): ConfigurationDirResult {
        // DSi requires the DS custom BIOS and firmware to be configured
        val dsiConfigurationResult = checkDsiConfigurationDirectory(settingsRepository.getDsiBiosDirectory())
        return if (dsiConfigurationResult.status != ConfigurationDirResult.Status.VALID) {
            dsiConfigurationResult
        } else {
            checkDsConfigurationDirectory()
        }
    }

    protected abstract fun checkDsConfigurationDirectory(directory: Uri?): ConfigurationDirResult
    protected abstract fun checkDsiConfigurationDirectory(directory: Uri?): ConfigurationDirResult

    fun checkConsoleConfigurationDirectory(consoleType: ConsoleType): ConfigurationDirResult {
        return when (consoleType) {
            ConsoleType.DS -> checkDsConfigurationDirectory()
            ConsoleType.DSi -> checkDsiConfigurationDirectory()
        }
    }

    fun checkConsoleConfigurationDirectory(consoleType: ConsoleType, directory: Uri?): ConfigurationDirResult {
        return when (consoleType) {
            ConsoleType.DS -> checkDsConfigurationDirectory(directory)
            ConsoleType.DSi -> checkDsiConfigurationDirectory(directory)
        }
    }
}