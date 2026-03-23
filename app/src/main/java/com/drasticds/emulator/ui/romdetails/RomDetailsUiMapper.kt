package com.drasticds.emulator.ui.romdetails

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.drasticds.emulator.domain.model.rom.config.RomConfig
import com.drasticds.emulator.domain.model.rom.config.RomGbaSlotConfig
import com.drasticds.emulator.domain.repositories.LayoutsRepository
import com.drasticds.emulator.ui.romdetails.model.RomConfigUiModel
import com.drasticds.emulator.ui.romdetails.model.RomGbaSlotConfigUiModel

class RomDetailsUiMapper(
    private val context: Context,
    private val layoutsRepository: LayoutsRepository,
) {

    suspend fun mapRomConfigToUi(romConfig: RomConfig): RomConfigUiModel {
        return RomConfigUiModel(
            runtimeConsoleType = romConfig.runtimeConsoleType,
            runtimeMicSource = romConfig.runtimeMicSource,
            layoutId = romConfig.layoutId,
            layoutName = romConfig.layoutId?.let { layoutsRepository.getLayout(it)?.name } ?: layoutsRepository.getGlobalLayoutPlaceholder().name,
            gbaSlotConfig = mapGbaSlotConfigToUi(romConfig.gbaSlotConfig),
            customName = romConfig.customName,
        )
    }

    private fun mapGbaSlotConfigToUi(gbaSlotConfig: RomGbaSlotConfig): RomGbaSlotConfigUiModel {
        return when (gbaSlotConfig) {
            is RomGbaSlotConfig.None -> RomGbaSlotConfigUiModel(type = RomGbaSlotConfigUiModel.Type.None)
            is RomGbaSlotConfig.GbaRom -> RomGbaSlotConfigUiModel(
                type = RomGbaSlotConfigUiModel.Type.GbaRom,
                gbaRomPath = gbaSlotConfig.romPath?.let { DocumentFile.fromSingleUri(context, it)?.name },
                gbaSavePath = gbaSlotConfig.savePath?.let { DocumentFile.fromSingleUri(context, it)?.name },
            )
            RomGbaSlotConfig.RumblePak -> RomGbaSlotConfigUiModel(type = RomGbaSlotConfigUiModel.Type.RumblePak)
            is RomGbaSlotConfig.MemoryExpansion -> RomGbaSlotConfigUiModel(type = RomGbaSlotConfigUiModel.Type.MemoryExpansion)
        }
    }
}
