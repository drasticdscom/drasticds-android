package com.drasticds.emulator.ui.emulator.render

fun interface FrameRenderCallback {
    fun renderFrame(isValidFrame: Boolean, frameTextureId: Int)
}