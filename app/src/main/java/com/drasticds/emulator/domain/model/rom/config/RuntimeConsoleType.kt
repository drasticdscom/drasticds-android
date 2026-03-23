package com.drasticds.emulator.domain.model.rom.config

import com.drasticds.emulator.domain.model.ConsoleType

enum class RuntimeConsoleType(val targetConsoleType: ConsoleType?) : RuntimeEnum<RuntimeConsoleType, ConsoleType> {
    DEFAULT(null),
    DS(ConsoleType.DS),
    DSi(ConsoleType.DSi);

    override fun getDefault() = DEFAULT
    override fun getValue() = targetConsoleType!!
}