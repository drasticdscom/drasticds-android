package com.drasticds.emulator.ui.common.componentbuilders

import android.content.Context
import androidx.core.content.ContextCompat
import com.drasticds.emulator.R

class TopScreenLayoutComponentViewBuilder : ScreenLayoutComponentViewBuilder() {
    override fun getBackgroundDrawable(context: Context) = ContextCompat.getDrawable(context, R.drawable.background_top_screen)
}