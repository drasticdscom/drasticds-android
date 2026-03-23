package com.drasticds.emulator.ui.emulator.input

import com.drasticds.emulator.domain.model.Input
import com.drasticds.emulator.domain.model.Point

interface IInputListener {
    fun onKeyPress(key: Input)
    fun onKeyReleased(key: Input)
    fun onTouch(point: Point)
}