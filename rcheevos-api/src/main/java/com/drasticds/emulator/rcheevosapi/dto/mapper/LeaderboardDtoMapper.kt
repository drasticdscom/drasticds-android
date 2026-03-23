package com.drasticds.emulator.rcheevosapi.dto.mapper

import com.drasticds.emulator.rcheevosapi.dto.LeaderboardDto
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import com.drasticds.emulator.rcheevosapi.model.RALeaderboard
import com.drasticds.emulator.rcheevosapi.model.RASetId

internal fun LeaderboardDto.mapToModel(gameId: RAGameId, setId: RASetId): RALeaderboard {
    return RALeaderboard(
        id = id,
        gameId = gameId,
        setId = setId,
        mem = mem,
        format = format,
        lowerIsBetter = lowerIsBetter,
        title = title,
        description = description,
        hidden = hidden,
    )
}