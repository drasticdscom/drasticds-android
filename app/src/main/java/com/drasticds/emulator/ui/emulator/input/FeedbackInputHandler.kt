package com.drasticds.emulator.ui.emulator.input

import android.view.HapticFeedbackConstants
import android.view.View
import com.drasticds.emulator.common.vibration.TouchVibrator

abstract class FeedbackInputHandler(inputListener: IInputListener, private val enableHapticFeedback: Boolean, private val touchVibrator: TouchVibrator) : BaseInputHandler(inputListener) {

    enum class HapticFeedbackType {
        KEY_PRESS,
        KEY_RELEASE
    }

    protected fun performHapticFeedback(view: View, type: HapticFeedbackType) {
        if (enableHapticFeedback) {
            val feedbackType = when (type) {
                HapticFeedbackType.KEY_PRESS -> HapticFeedbackConstants.KEYBOARD_TAP
                HapticFeedbackType.KEY_RELEASE -> HapticFeedbackConstants.CLOCK_TICK
            }
            view.performHapticFeedback(feedbackType)
        }
    }
}