package com.drasticds.emulator.common.cheats

import com.drasticds.emulator.domain.model.CheatDatabase
import com.drasticds.emulator.domain.model.Game

interface CheatDatabaseParserListener {
    fun onDatabaseParseStart(databaseName: String): CheatDatabase
    fun onGameParseStart(gameName: String)
    fun onGameParsed(game: Game)
    fun onParseComplete()
}