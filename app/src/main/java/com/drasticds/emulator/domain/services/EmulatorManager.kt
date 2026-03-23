package com.drasticds.emulator.domain.services

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import com.drasticds.emulator.domain.model.Cheat
import com.drasticds.emulator.domain.model.ConsoleType
import com.drasticds.emulator.domain.model.emulator.EmulatorEvent
import com.drasticds.emulator.domain.model.emulator.FirmwareLaunchResult
import com.drasticds.emulator.domain.model.emulator.RomLaunchResult
import com.drasticds.emulator.domain.model.retroachievements.GameAchievementData
import com.drasticds.emulator.domain.model.retroachievements.RAEvent
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.ui.emulator.rewind.model.RewindSaveState
import com.drasticds.emulator.ui.emulator.rewind.model.RewindWindow

interface EmulatorManager {

    val emulatorEvents: Flow<EmulatorEvent>

    suspend fun loadRom(rom: Rom, cheats: List<Cheat>): RomLaunchResult

    suspend fun loadFirmware(consoleType: ConsoleType): FirmwareLaunchResult

    suspend fun updateRomEmulatorConfiguration(rom: Rom)

    suspend fun updateFirmwareEmulatorConfiguration(consoleType: ConsoleType)

    suspend fun getRewindWindow(): RewindWindow

    fun getFps(): Float

    suspend fun pauseEmulator()

    suspend fun resumeEmulator()

    suspend fun resetEmulator()

    suspend fun updateCheats(cheats: List<Cheat>)
    suspend fun setupRetroAchievements(achievementData: GameAchievementData)
    fun unloadRetroAchievementsData()

    suspend fun loadRewindState(rewindSaveState: RewindSaveState): Boolean

    suspend fun saveState(saveStateFileUri: Uri): Boolean

    suspend fun loadState(saveStateFileUri: Uri): Boolean

    fun stopEmulator()

    fun cleanEmulator()

    fun observeRetroAchievementEvents(): Flow<RAEvent>
}