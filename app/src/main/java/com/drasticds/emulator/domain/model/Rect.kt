package com.drasticds.emulator.domain.model

data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) {

    val bottom get() = y + height

    val right get() = x + width
}