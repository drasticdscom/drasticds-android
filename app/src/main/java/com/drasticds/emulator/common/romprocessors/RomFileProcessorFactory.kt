package com.drasticds.emulator.common.romprocessors

import android.net.Uri
import androidx.documentfile.provider.DocumentFile

interface RomFileProcessorFactory {
    fun getFileRomProcessorForDocument(romDocument: DocumentFile): RomFileProcessor?
    fun getFileRomProcessorForDocument(romUri: Uri): RomFileProcessor?
}