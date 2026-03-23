package com.drasticds.emulator.migrations.legacy

import android.net.Uri
import com.google.gson.annotations.SerializedName
import com.drasticds.emulator.domain.model.rom.config.RuntimeConsoleType
import com.drasticds.emulator.domain.model.rom.config.RuntimeMicSource
import java.util.*

data class RomConfig1(
    @SerializedName("a")
    val runtimeConsoleType: RuntimeConsoleType,
    @SerializedName("b")
    val runtimeMicSource: RuntimeMicSource,
    @SerializedName("c")
    val layoutId: UUID?,
    @SerializedName("d")
    val loadGbaCart: Boolean,
    @SerializedName("e")
    val gbaCartPath: Uri?,
    @SerializedName("f")
    val gbaSavePath: Uri?,
)
