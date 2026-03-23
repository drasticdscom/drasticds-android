package com.drasticds.emulator.domain.repositories

import android.graphics.Bitmap
import android.net.Uri
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.SaveStateSlot

interface SaveStatesRepository {
    fun getRomSaveStates(rom: Rom): List<SaveStateSlot>
    fun getRomQuickSaveStateSlot(rom: Rom): SaveStateSlot
    fun getRomSaveStateUri(rom: Rom, saveState: SaveStateSlot): Uri
    fun setRomSaveStateScreenshot(rom: Rom, saveState: SaveStateSlot, screenshot: Bitmap)
    fun deleteRomSaveState(rom: Rom, saveState: SaveStateSlot)
}