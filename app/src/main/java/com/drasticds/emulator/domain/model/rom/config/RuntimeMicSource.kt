package com.drasticds.emulator.domain.model.rom.config

import com.drasticds.emulator.domain.model.MicSource

enum class RuntimeMicSource(val micSource: MicSource?) : RuntimeEnum<RuntimeMicSource, MicSource> {
    DEFAULT(null),
    NONE(MicSource.NONE),
    BLOW(MicSource.BLOW),
    DEVICE(MicSource.DEVICE);

    override fun getDefault() = DEFAULT
    override fun getValue() = micSource!!
}
