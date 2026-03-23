package com.drasticds.emulator.ui.emulator

import com.drasticds.emulator.domain.model.render.FrameRenderEvent

interface FrameRenderEventConsumer {
    fun prepareNextFrame(frameRenderEvent: FrameRenderEvent)
}