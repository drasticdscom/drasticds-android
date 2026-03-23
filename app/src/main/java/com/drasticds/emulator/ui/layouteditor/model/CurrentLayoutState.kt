package com.drasticds.emulator.ui.layouteditor.model

import com.drasticds.emulator.domain.model.layout.LayoutConfiguration
import com.drasticds.emulator.domain.model.layout.UILayout

data class CurrentLayoutState(
    val layout: UILayout,
    val orientation: LayoutConfiguration.LayoutOrientation,
)