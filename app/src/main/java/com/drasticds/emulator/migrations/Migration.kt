package com.drasticds.emulator.migrations

interface Migration {
    val from: Int
    val to: Int

    fun migrate()
}