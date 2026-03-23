package com.drasticds.emulator.migrations.legacy.input

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.drasticds.emulator.domain.model.Input

@Serializable
data class InputConfigDto33(
    @SerialName("a") val input: Input,
    @SerialName("b") val key: Int,
)