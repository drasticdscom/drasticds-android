package com.drasticds.emulator.ui.emulator.render

import com.drasticds.emulator.domain.model.render.PresentFrameWrapper
import com.drasticds.emulator.ui.emulator.model.RuntimeRendererConfiguration

interface EmulatorRenderer {
    fun updateRendererConfiguration(newRendererConfiguration: RuntimeRendererConfiguration?)
    fun setLeftRotationEnabled(enabled: Boolean)
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun drawFrame(presentFrameWrapper: PresentFrameWrapper)
}