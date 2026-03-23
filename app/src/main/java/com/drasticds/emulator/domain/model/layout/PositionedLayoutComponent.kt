package com.drasticds.emulator.domain.model.layout

import com.drasticds.emulator.domain.model.Rect

data class PositionedLayoutComponent(
    val rect: Rect,
    val component: LayoutComponent,
    val alpha: Float = 1f,
    val onTop: Boolean = false,
) {

    fun isScreen(): Boolean {
        return component.isScreen()
    }
}