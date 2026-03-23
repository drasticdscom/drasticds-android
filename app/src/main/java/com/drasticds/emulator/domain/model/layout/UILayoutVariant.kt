package com.drasticds.emulator.domain.model.layout

import com.drasticds.emulator.domain.model.Point
import com.drasticds.emulator.domain.model.ui.Orientation

data class UILayoutVariant(
    val uiSize: Point,
    val orientation: Orientation,
    val folds: List<ScreenFold>,
    val displays: LayoutDisplayPair,
)