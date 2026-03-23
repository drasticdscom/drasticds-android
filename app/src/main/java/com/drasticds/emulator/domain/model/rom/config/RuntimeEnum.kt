package com.drasticds.emulator.domain.model.rom.config

interface RuntimeEnum<T, U> {
    fun getDefault(): T
    fun getValue(): U
}