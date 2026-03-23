package com.drasticds.emulator.domain.model.render

data class FrameRenderEvent(
    val isValidFrame: Boolean,
    val textureId: Int,
)
