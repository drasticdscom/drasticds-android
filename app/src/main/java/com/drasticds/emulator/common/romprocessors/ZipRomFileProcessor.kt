package com.drasticds.emulator.common.romprocessors

import android.content.Context
import com.drasticds.emulator.common.uridelegates.UriHandler
import com.drasticds.emulator.domain.model.SizeUnit
import com.drasticds.emulator.impl.NdsRomCache
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ZipRomFileProcessor(context: Context, uriHandler: UriHandler, ndsRomCache: NdsRomCache) : CompressedRomFileProcessor(context, uriHandler, ndsRomCache) {

    override fun getNdsEntryStreamInFileStream(fileStream: InputStream): RomFileStream? {
        val zipStream = ZipInputStream(fileStream)
        return getNdsEntryInZipStream(zipStream)?.let {
            // ZIP ROMs seem to require the stream to be buffered, otherwise data is not processed properly
            RomFileStream(zipStream.buffered(), SizeUnit.Bytes(it.size))
        }
    }

    private fun getNdsEntryInZipStream(inputStream: ZipInputStream): ZipEntry? {
        do {
            val nextEntry = inputStream.nextEntry ?: break
            if (!nextEntry.isDirectory && isSupportedRomFile(nextEntry.name)) {
                return nextEntry
            }
        } while (true)
        return null
    }
}