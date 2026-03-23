package com.drasticds.emulator.domain.model.emulator

import com.drasticds.emulator.MelonEmulator

sealed class RomLaunchResult {
    data object LaunchFailedRomNotFound : RomLaunchResult()
    data class LaunchFailedSramProblem(val reason: Exception) : RomLaunchResult()
    data class LaunchFailed(val reason: MelonEmulator.LoadResult) : RomLaunchResult()
    data class LaunchSuccessful(val isGbaLoadSuccessful: Boolean) : RomLaunchResult()
}