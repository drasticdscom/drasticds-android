package com.drasticds.emulator.domain.model

const val SCREEN_WIDTH = 256
const val SCREEN_HEIGHT = 192

val consoleAspectRatio: Float get() {
    return SCREEN_WIDTH.toFloat() / SCREEN_HEIGHT
}