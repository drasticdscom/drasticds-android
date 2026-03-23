package com.drasticds.emulator.ui.emulator.component

import com.drasticds.emulator.ui.emulator.model.EmulatorOverlay

class EmulatorOverlayTracker(
    private val onOverlaysCleared: () -> Unit,
    private val onOverlaysPresent: () -> Unit,
) {

    private val activeOverlays = mutableListOf<EmulatorOverlay>()

    fun addActiveOverlay(overlay: EmulatorOverlay) {
        activeOverlays.add(overlay)
        if (activeOverlays.size == 1) {
            onOverlaysPresent()
        }
    }

    fun removeActiveOverlay(overlay: EmulatorOverlay) {
        activeOverlays.remove(overlay)
        if (activeOverlays.isEmpty()) {
            onOverlaysCleared()
        }
    }

    fun hasActiveOverlays(): Boolean {
        return activeOverlays.isNotEmpty()
    }
}