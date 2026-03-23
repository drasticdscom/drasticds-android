package com.drasticds.emulator.impl.mappers.retroachievements

import com.drasticds.emulator.database.entities.retroachievements.RAAchievementSetEntity
import com.drasticds.emulator.rcheevosapi.model.RAAchievement
import com.drasticds.emulator.rcheevosapi.model.RAAchievementSet
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import com.drasticds.emulator.rcheevosapi.model.RALeaderboard
import com.drasticds.emulator.rcheevosapi.model.RASetId
import java.net.URI

fun RAAchievementSet.mapToEntity(): RAAchievementSetEntity {
    return RAAchievementSetEntity(
        id = id.id,
        gameId = gameId.id,
        title = title,
        type = type.name,
        iconUrl = iconUrl.toString(),
    )
}

fun RAAchievementSetEntity.mapToModel(achievements: List<RAAchievement>, leaderboards: List<RALeaderboard>): RAAchievementSet {
    return RAAchievementSet(
        id = RASetId(id),
        gameId = RAGameId(gameId),
        title = title,
        type = RAAchievementSet.Type.valueOf(type),
        iconUrl = URI(iconUrl).toURL(),
        achievements = achievements,
        leaderboards = leaderboards,
    )
}