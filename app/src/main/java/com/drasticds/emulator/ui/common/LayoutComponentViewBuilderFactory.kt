package com.drasticds.emulator.ui.common

import com.drasticds.emulator.domain.model.layout.LayoutComponent

interface LayoutComponentViewBuilderFactory {
    fun getLayoutComponentViewBuilder(layoutComponent: LayoutComponent): LayoutComponentViewBuilder
}