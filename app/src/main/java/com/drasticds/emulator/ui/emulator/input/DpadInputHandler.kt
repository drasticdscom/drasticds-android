package com.drasticds.emulator.ui.emulator.input

import com.drasticds.emulator.common.vibration.TouchVibrator
import com.drasticds.emulator.domain.model.Input

class DpadInputHandler(inputListener: IInputListener, enableHapticFeedback: Boolean, touchVibrator: TouchVibrator) : MultiButtonInputHandler(inputListener, enableHapticFeedback, touchVibrator) {
    override fun getTopInput() = Input.UP
    override fun getLeftInput() = Input.LEFT
    override fun getBottomInput() = Input.DOWN
    override fun getRightInput() = Input.RIGHT
}