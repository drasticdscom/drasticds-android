package com.drasticds.emulator.ui.emulator.input

import com.drasticds.emulator.common.vibration.TouchVibrator
import com.drasticds.emulator.domain.model.Input

class ButtonsInputHandler(inputListener: IInputListener, enableHapticFeedback: Boolean, touchVibrator: TouchVibrator) : MultiButtonInputHandler(inputListener, enableHapticFeedback, touchVibrator) {
    override fun getTopInput() = Input.X
    override fun getLeftInput() = Input.Y
    override fun getBottomInput() = Input.B
    override fun getRightInput() = Input.A
}