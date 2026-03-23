package com.drasticds.emulator.impl.mappers.retroachievements

import com.drasticds.emulator.database.entities.retroachievements.RAGameWithSetsEntity
import com.drasticds.emulator.rcheevosapi.model.RAGame
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import java.net.URL

fun RAGameWithSetsEntity.mapToModel(): RAGame {
    return RAGame(
        id = RAGameId(game.gameId),
        richPresencePatch = game.richPresencePatch,
        title = game.title,
        icon = URL(game.icon),
        sets = sets.map { setWithData ->
            val achievements = setWithData.achievements.map { it.mapToModel() }
            val leaderboards = setWithData.leaderboards.map { it.mapToModel() }
            setWithData.set.mapToModel(achievements, leaderboards)
        },
    )
}