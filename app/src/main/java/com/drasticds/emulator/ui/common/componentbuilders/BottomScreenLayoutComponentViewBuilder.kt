package com.drasticds.emulator.ui.common.componentbuilders

import android.content.Context
import androidx.core.content.ContextCompat
import com.drasticds.emulator.R

class BottomScreenLayoutComponentViewBuilder : ScreenLayoutComponentViewBuilder() {
    override fun getBackgroundDrawable(context: Context) = ContextCompat.getDrawable(context, R.drawable.background_bottom_screen)
}