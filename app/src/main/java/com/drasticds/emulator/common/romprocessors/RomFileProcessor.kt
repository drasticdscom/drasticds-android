package com.drasticds.emulator.common.romprocessors

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Single
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.RomInfo

interface RomFileProcessor {
    fun getRomFromUri(romUri: Uri, parentUri: Uri?): Rom?
    fun getRomIcon(rom: Rom): Bitmap?
    fun getRomInfo(rom: Rom): RomInfo?
    fun getRealRomUri(rom: Rom): Single<Uri>
}