package com.drasticds.emulator

import com.drasticds.emulator.common.UriFileHandler

object MelonDSAndroidInterface {
    external fun setup(uriFileHandler: UriFileHandler)
    external fun getEmulatorGlContext(): Long
    external fun cleanup()
}