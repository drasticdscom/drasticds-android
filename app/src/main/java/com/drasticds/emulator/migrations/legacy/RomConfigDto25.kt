package com.drasticds.emulator.migrations.legacy

import com.google.gson.annotations.SerializedName
import com.drasticds.emulator.domain.model.rom.config.RuntimeConsoleType
import com.drasticds.emulator.domain.model.rom.config.RuntimeMicSource

data class RomConfigDto25(
    @SerializedName("runtimeConsoleType")
    val runtimeConsoleType: RuntimeConsoleType,
    @SerializedName("runtimeMicSource")
    val runtimeMicSource: RuntimeMicSource,
    @SerializedName("layoutId")
    val layoutId: String?,
    @SerializedName("loadGbaCart")
    val loadGbaCart: Boolean,
    @SerializedName("gbaCartPath")
    val gbaCartPath: String?,
    @SerializedName("gbaSavePath")
    val gbaSavePath: String?,
)
