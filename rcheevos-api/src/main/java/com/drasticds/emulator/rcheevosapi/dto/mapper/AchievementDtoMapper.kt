package com.drasticds.emulator.rcheevosapi.dto.mapper

import com.drasticds.emulator.rcheevosapi.dto.AchievementDto
import com.drasticds.emulator.rcheevosapi.model.RAAchievement
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import com.drasticds.emulator.rcheevosapi.model.RASetId
import java.net.URI

internal fun AchievementDto.mapToModel(gameId: RAGameId, setId: RASetId): RAAchievement {
    return RAAchievement(
        id = id,
        gameId = gameId,
        setId = setId,
        totalAwardsCasual = numAwarded,
        totalAwardsHardcore = numAwardedHardcore,
        title = title,
        description = description,
        points = points,
        displayOrder = displayOrder?.toIntOrNull() ?: 0,
        badgeUrlUnlocked = URI(badgeUrl).toURL(),
        badgeUrlLocked = URI(badgeUrlLocked).toURL(),
        memoryAddress = memoryAddress,
        type = achievementFlagsToType(flags),
    )
}

private fun achievementFlagsToType(achievementFlags: Int): RAAchievement.Type {
    return when (achievementFlags) {
        3 -> RAAchievement.Type.CORE
        else -> RAAchievement.Type.UNOFFICIAL
    }
}