package com.drasticds.emulator.common.romprocessors

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Single
import com.drasticds.emulator.common.uridelegates.UriHandler
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.rom.config.RomConfig
import com.drasticds.emulator.domain.model.RomInfo
import com.drasticds.emulator.domain.model.RomMetadata
import com.drasticds.emulator.extensions.isBlank
import com.drasticds.emulator.extensions.nameWithoutExtension
import com.drasticds.emulator.utils.RomProcessor

class NdsRomFileProcessor(private val context: Context, private val uriHandler: UriHandler) : RomFileProcessor {

    override fun getRomFromUri(romUri: Uri, parentUri: Uri?): Rom? {
        return try {
            getRomMetadata(romUri)?.let { metadata ->
                val romDocument = uriHandler.getUriDocument(romUri)
                val romName = metadata.romTitle.takeUnless { it.isBlank() } ?: romDocument?.nameWithoutExtension ?: ""
                Rom(
                    name = romName,
                    developerName = metadata.developerName,
                    fileName = romDocument?.name ?: "",
                    uri = romUri,
                    parentTreeUri = parentUri,
                    config = if (metadata.isDSiWareTitle) RomConfig.forDsiWareTitle() else RomConfig.default(),
                    lastPlayed = null,
                    isDsiWareTitle = metadata.isDSiWareTitle,
                    retroAchievementsHash = metadata.retroAchievementsHash
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getRomIcon(rom: Rom): Bitmap? {
        return try {
            context.contentResolver.openInputStream(rom.uri)?.use { inputStream ->
                RomProcessor.getRomIcon(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getRomInfo(rom: Rom): RomInfo? {
        return try {
            context.contentResolver.openInputStream(rom.uri)?.use { inputStream ->
                RomProcessor.getRomInfo(rom, inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getRealRomUri(rom: Rom): Single<Uri> {
        return Single.just(rom.uri)
    }

    private fun getRomMetadata(uri: Uri): RomMetadata? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            RomProcessor.getRomMetadata(inputStream)
        }
    }
}