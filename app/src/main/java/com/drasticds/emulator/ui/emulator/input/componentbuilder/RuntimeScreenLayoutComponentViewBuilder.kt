package com.drasticds.emulator.ui.emulator.input.componentbuilder

import android.content.Context
import android.graphics.drawable.Drawable
import com.drasticds.emulator.ui.common.componentbuilders.ScreenLayoutComponentViewBuilder

class RuntimeScreenLayoutComponentViewBuilder : ScreenLayoutComponentViewBuilder() {
    override fun getBackgroundDrawable(context: Context): Drawable? = null
}