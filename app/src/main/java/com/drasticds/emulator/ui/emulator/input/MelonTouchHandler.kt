package com.drasticds.emulator.ui.emulator.input

import com.drasticds.emulator.MelonEmulator
import com.drasticds.emulator.domain.model.Input
import com.drasticds.emulator.domain.model.Point

class MelonTouchHandler : IInputListener {
    private var isLidClosed = false

    override fun onKeyPress(key: Input) {
        if (key == Input.HINGE) {
            handleHingePress()
        } else {
            MelonEmulator.onInputDown(key)
        }
    }

    override fun onKeyReleased(key: Input) {
        if (key != Input.HINGE) {
            MelonEmulator.onInputUp(key)
        }
    }

    override fun onTouch(point: Point) {
        MelonEmulator.onScreenTouch(point.x, point.y)
    }

    private fun handleHingePress() {
        isLidClosed = !isLidClosed
        if (isLidClosed) {
            MelonEmulator.onInputDown(Input.HINGE)
        } else {
            MelonEmulator.onInputUp(Input.HINGE)
        }
    }
}