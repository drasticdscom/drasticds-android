package com.drasticds.emulator.ui.emulator.model

import com.drasticds.emulator.domain.model.VideoFiltering

data class RuntimeRendererConfiguration(
    val videoFiltering: VideoFiltering,
    val resolutionScaling: Int,
)