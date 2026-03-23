package com.drasticds.emulator.impl.layout.devicemapper

import android.content.Context
import android.view.Display
import com.drasticds.emulator.domain.model.layout.LayoutDisplay
import com.drasticds.emulator.domain.model.layout.LayoutDisplayPair
import com.drasticds.emulator.impl.layout.DeviceLayoutDisplayMapper

class DefaultLayoutDisplayMapper(context: Context) : DeviceLayoutDisplayMapper(context) {

    override fun mapDisplaysToLayoutDisplays(currentDisplay: Display, secondaryDisplay: Display?): LayoutDisplayPair {
        val mainLayoutDisplay = mapDisplayToLayoutDisplay(
            display = currentDisplay,
            displayType = if (currentDisplay.name == "Built-in Screen") LayoutDisplay.Type.BUILT_IN else LayoutDisplay.Type.EXTERNAL,
        )
        val secondaryLayoutDisplay = secondaryDisplay?.let {
            mapDisplayToLayoutDisplay(
                display = it,
                displayType = if (it.name == "Built-in Screen") LayoutDisplay.Type.BUILT_IN else LayoutDisplay.Type.EXTERNAL,
            )
        }

        return LayoutDisplayPair(mainLayoutDisplay, secondaryLayoutDisplay)
    }
}