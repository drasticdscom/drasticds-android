package com.drasticds.emulator.impl.dtos.rom

import com.google.gson.annotations.SerializedName
import com.drasticds.emulator.domain.model.rom.config.RomConfig
import com.drasticds.emulator.domain.model.rom.config.RuntimeConsoleType
import com.drasticds.emulator.domain.model.rom.config.RuntimeMicSource
import java.util.UUID

data class RomConfigDto(
    @SerializedName("runtimeConsoleType")
    val runtimeConsoleType: RuntimeConsoleType,
    @SerializedName("runtimeMicSource")
    val runtimeMicSource: RuntimeMicSource,
    @SerializedName("layoutId")
    val layoutId: String?,
    @SerializedName("gbaSlotConfig")
    val gbaSlotConfig: RomGbaSlotConfigDto,
    @SerializedName("customName")
    val customName: String? = null,
) {

    companion object {
        fun fromModel(romConfig: RomConfig): RomConfigDto {
            return RomConfigDto(
                romConfig.runtimeConsoleType,
                romConfig.runtimeMicSource,
                romConfig.layoutId?.toString(),
                RomGbaSlotConfigDto.fromModel(romConfig.gbaSlotConfig),
                romConfig.customName,
            )
        }
    }

    fun toModel(): RomConfig {
        return RomConfig(
            runtimeConsoleType,
            runtimeMicSource,
            layoutId?.let { UUID.fromString(it) },
            gbaSlotConfig.toModel(),
            customName = customName,
        )
    }
}