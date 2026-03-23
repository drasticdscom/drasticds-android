package com.drasticds.emulator.ui.emulator.input

import android.view.MotionEvent
import android.view.View
import com.drasticds.emulator.common.vibration.TouchVibrator
import com.drasticds.emulator.domain.model.Input

class SingleButtonInputHandler(inputListener: IInputListener, private val input: Input, enableHapticFeedback: Boolean, touchVibrator: TouchVibrator) : FeedbackInputHandler(inputListener, enableHapticFeedback, touchVibrator) {
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                inputListener.onKeyPress(input)
                performHapticFeedback(v, HapticFeedbackType.KEY_PRESS)
            }
            MotionEvent.ACTION_UP -> {
                inputListener.onKeyReleased(input)
                performHapticFeedback(v, HapticFeedbackType.KEY_RELEASE)
            }
        }
        return true
    }
}