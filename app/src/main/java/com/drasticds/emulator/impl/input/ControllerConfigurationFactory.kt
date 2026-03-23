package com.drasticds.emulator.impl.input

import com.drasticds.emulator.domain.model.ControllerConfiguration

interface ControllerConfigurationFactory {
    fun buildDefaultControllerConfiguration(): ControllerConfiguration
}