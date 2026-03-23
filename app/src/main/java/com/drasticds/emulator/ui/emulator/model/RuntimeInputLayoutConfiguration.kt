package com.drasticds.emulator.ui.emulator.model

import com.drasticds.emulator.domain.model.input.SoftInputBehaviour
import com.drasticds.emulator.domain.model.layout.LayoutConfiguration
import com.drasticds.emulator.domain.model.layout.UILayout

data class RuntimeInputLayoutConfiguration(
    val softInputBehaviour: SoftInputBehaviour,
    val softInputOpacity: Int,
    val isHapticFeedbackEnabled: Boolean,
    val layoutOrientation: LayoutConfiguration.LayoutOrientation,
    val layout: UILayout,
)