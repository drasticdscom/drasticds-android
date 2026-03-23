package com.drasticds.emulator.ui.emulator.firmware

import com.drasticds.emulator.R
import com.drasticds.emulator.ui.emulator.PauseMenuOption

enum class FirmwarePauseMenuOption(override val textResource: Int) : PauseMenuOption {
    SETTINGS(R.string.settings),
    RESET(R.string.reset),
    EXIT(R.string.exit)
}