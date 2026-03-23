package com.drasticds.emulator.ui.common.componentbuilders

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.drasticds.emulator.R
import com.drasticds.emulator.ui.common.LayoutComponentViewBuilder

class DpadLayoutComponentViewBuilder : LayoutComponentViewBuilder() {
    override fun build(context: Context): View {
        return ImageView(context).apply {
            setImageResource(R.drawable.keypad)
        }
    }

    override fun getAspectRatio() = 1f
}