package com.drasticds.emulator.domain.model.layout

import com.drasticds.emulator.domain.model.Rect
import com.drasticds.emulator.domain.model.ui.Orientation

data class ScreenFold(
    val orientation: Orientation,
    val type: FoldType,
    val foldBounds: Rect,
) {
    
    enum class FoldType {
        SEAMLESS,
        GAP,
    }
}