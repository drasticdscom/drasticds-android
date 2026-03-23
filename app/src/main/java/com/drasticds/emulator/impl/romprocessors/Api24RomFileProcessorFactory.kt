package com.drasticds.emulator.impl.romprocessors

import android.content.Context
import com.drasticds.emulator.common.romprocessors.NdsRomFileProcessor
import com.drasticds.emulator.common.romprocessors.RomFileProcessor
import com.drasticds.emulator.common.romprocessors.SevenZRomFileProcessor
import com.drasticds.emulator.common.romprocessors.ZipRomFileProcessor
import com.drasticds.emulator.common.uridelegates.UriHandler
import com.drasticds.emulator.impl.NdsRomCache

class Api24RomFileProcessorFactory(context: Context, uriHandler: UriHandler, ndsRomCache: NdsRomCache) : BaseRomFileProcessorFactory(context) {

    private val prefixProcessorMap: Map<String, RomFileProcessor>

    init {
        val ndsRomFileProcessor = NdsRomFileProcessor(context, uriHandler)
        prefixProcessorMap = mapOf(
            "nds" to ndsRomFileProcessor,
            "dsi" to ndsRomFileProcessor,
            "ids" to ndsRomFileProcessor,
            "zip" to ZipRomFileProcessor(context, uriHandler, ndsRomCache),
            "7z" to SevenZRomFileProcessor(context, uriHandler, ndsRomCache)
        )
    }

    override fun getRomFileProcessorForFileExtension(extension: String): RomFileProcessor? {
        return prefixProcessorMap[extension]
    }
}