package com.drasticds.emulator.ui.shortcutsetup

import com.drasticds.emulator.domain.model.ConsoleType

class DSiFirmwareShortcutSetupActivity : FirmwareShortcutSetupActivity() {
    override fun getConsoleType(): ConsoleType {
        return ConsoleType.DSi
    }
}