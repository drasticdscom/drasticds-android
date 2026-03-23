package com.drasticds.emulator.common.cheats

interface CheatDatabaseParser {
    fun parseCheatDatabase(databaseStream: ProgressTrackerInputStream, parseListener: CheatDatabaseParserListener)
}