package com.drasticds.emulator.impl.mappers.retroachievements

import com.drasticds.emulator.database.entities.retroachievements.RALeaderboardEntity
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import com.drasticds.emulator.rcheevosapi.model.RALeaderboard
import com.drasticds.emulator.rcheevosapi.model.RASetId

fun RALeaderboard.mapToEntity(): RALeaderboardEntity {
    return RALeaderboardEntity(
        id = id,
        gameId = gameId.id,
        setId = setId.id,
        mem = mem,
        format = format,
        lowerIsBetter = lowerIsBetter,
        title = title,
        description = description,
        hidden = hidden,
    )
}

fun RALeaderboardEntity.mapToModel(): RALeaderboard {
    return RALeaderboard(
        id = id,
        gameId = RAGameId(gameId),
        setId = RASetId(setId),
        mem = mem,
        format = format,
        lowerIsBetter = lowerIsBetter,
        title = title,
        description = description,
        hidden = hidden,
    )
}