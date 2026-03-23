package com.drasticds.emulator.migrations.legacy.layout

import com.google.gson.annotations.SerializedName
import com.drasticds.emulator.impl.dtos.layout.PositionedLayoutComponentDto

/**
 * UILayoutDto used until app version 35.
 */
data class UILayoutDto35(
    @SerializedName("backgroundId")
    val backgroundId: String?,
    @SerializedName("backgroundMode")
    val backgroundMode: String,
    @SerializedName("components")
    val components: List<PositionedLayoutComponentDto>?,
)
