package com.drasticds.emulator.ui.layouteditor

import com.drasticds.emulator.domain.model.layout.LayoutComponent
import com.drasticds.emulator.ui.common.LayoutComponentViewBuilder
import com.drasticds.emulator.ui.common.LayoutComponentViewBuilderFactory
import com.drasticds.emulator.ui.common.componentbuilders.*

class EditorLayoutComponentViewBuilderFactory : LayoutComponentViewBuilderFactory {
    private val layoutComponentViewBuilderCache = mutableMapOf<LayoutComponent, LayoutComponentViewBuilder>()

    override fun getLayoutComponentViewBuilder(layoutComponent: LayoutComponent): LayoutComponentViewBuilder {
        return layoutComponentViewBuilderCache.getOrElse(layoutComponent) {
            val builder = when (layoutComponent) {
                LayoutComponent.TOP_SCREEN -> TopScreenLayoutComponentViewBuilder()
                LayoutComponent.BOTTOM_SCREEN -> BottomScreenLayoutComponentViewBuilder()
                LayoutComponent.DPAD -> EditorBackgroundLayoutComponentViewBuilder(DpadLayoutComponentViewBuilder())
                LayoutComponent.BUTTONS -> EditorBackgroundLayoutComponentViewBuilder(ButtonsLayoutComponentViewBuilder())
                else -> EditorBackgroundLayoutComponentViewBuilder(SingleButtonLayoutComponentViewBuilder(layoutComponent))
            }

            layoutComponentViewBuilderCache[layoutComponent] = builder
            builder
        }
    }
}