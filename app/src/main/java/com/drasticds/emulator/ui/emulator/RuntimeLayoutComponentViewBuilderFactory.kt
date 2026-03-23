package com.drasticds.emulator.ui.emulator

import com.drasticds.emulator.domain.model.layout.LayoutComponent
import com.drasticds.emulator.ui.common.LayoutComponentViewBuilder
import com.drasticds.emulator.ui.common.LayoutComponentViewBuilderFactory
import com.drasticds.emulator.ui.common.componentbuilders.*
import com.drasticds.emulator.ui.emulator.input.componentbuilder.RuntimeScreenLayoutComponentViewBuilder
import com.drasticds.emulator.ui.emulator.input.componentbuilder.ToggleableSingleButtonLayoutComponentViewBuilder

class RuntimeLayoutComponentViewBuilderFactory : LayoutComponentViewBuilderFactory {
    private val layoutComponentViewBuilderCache = mutableMapOf<LayoutComponent, LayoutComponentViewBuilder>()

    override fun getLayoutComponentViewBuilder(layoutComponent: LayoutComponent): LayoutComponentViewBuilder {
        return layoutComponentViewBuilderCache.getOrElse(layoutComponent) {
            val builder = when (layoutComponent) {
                LayoutComponent.TOP_SCREEN -> RuntimeScreenLayoutComponentViewBuilder()
                LayoutComponent.BOTTOM_SCREEN -> RuntimeScreenLayoutComponentViewBuilder()
                LayoutComponent.DPAD -> DpadLayoutComponentViewBuilder()
                LayoutComponent.BUTTONS -> ButtonsLayoutComponentViewBuilder()
                LayoutComponent.BUTTON_FAST_FORWARD_TOGGLE,
                LayoutComponent.BUTTON_MICROPHONE_TOGGLE,
                LayoutComponent.BUTTON_TOGGLE_SOFT_INPUT -> ToggleableSingleButtonLayoutComponentViewBuilder(layoutComponent)
                else -> SingleButtonLayoutComponentViewBuilder(layoutComponent)
            }

            layoutComponentViewBuilderCache[layoutComponent] = builder
            builder
        }
    }
}