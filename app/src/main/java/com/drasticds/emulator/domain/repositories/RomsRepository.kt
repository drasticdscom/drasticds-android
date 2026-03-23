package com.drasticds.emulator.domain.repositories

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.rom.config.RomConfig
import com.drasticds.emulator.domain.model.RomScanningStatus
import java.util.*
import kotlin.time.Duration

interface RomsRepository {
    fun getRoms(): Flow<List<Rom>>
    fun getRomScanningStatus(): StateFlow<RomScanningStatus>
    suspend fun getRomAtPath(path: String): Rom?
    suspend fun getRomAtUri(uri: Uri): Rom?

    fun updateRomConfig(rom: Rom, romConfig: RomConfig)
    fun setRomLastPlayed(rom: Rom, lastPlayed: Date)
    fun addRomPlayTime(rom: Rom, playTime: Duration)
    fun rescanRoms()
    fun invalidateRoms()
}
