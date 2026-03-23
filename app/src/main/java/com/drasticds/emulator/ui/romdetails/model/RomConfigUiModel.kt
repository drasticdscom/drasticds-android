package com.drasticds.emulator.ui.romdetails.model

import com.drasticds.emulator.domain.model.rom.config.RuntimeConsoleType
import com.drasticds.emulator.domain.model.rom.config.RuntimeMicSource
import java.util.UUID

data class RomConfigUiModel(
    val runtimeConsoleType: RuntimeConsoleType = RuntimeConsoleType.DEFAULT,
    val runtimeMicSource: RuntimeMicSource = RuntimeMicSource.DEFAULT,
    val layoutId: UUID? = null,
    val layoutName: String? = null,
    val gbaSlotConfig: RomGbaSlotConfigUiModel = RomGbaSlotConfigUiModel(),
    val customName: String? = null,
)
