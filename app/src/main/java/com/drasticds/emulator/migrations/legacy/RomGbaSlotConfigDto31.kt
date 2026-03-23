package com.drasticds.emulator.migrations.legacy

import com.google.gson.annotations.SerializedName

class RomGbaSlotConfigDto31(
    @SerializedName("type")
    val type: Type,
    @SerializedName("gbaRomPath")
    val gbaRomPath: String?,
    @SerializedName("gbaSavePath")
    val gbaSavePath: String?,
) {

    enum class Type {
        None, GbaRom, MemoryExpansion
    }
}
