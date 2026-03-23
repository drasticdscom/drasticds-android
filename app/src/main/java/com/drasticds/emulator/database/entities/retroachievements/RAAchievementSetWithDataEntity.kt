package com.drasticds.emulator.database.entities.retroachievements

import androidx.room.Embedded
import androidx.room.Relation

data class RAAchievementSetWithDataEntity(
    @Embedded val set: RAAchievementSetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "set_id",
    )
    val achievements: List<RAAchievementEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "set_id",
    )
    val leaderboards: List<RALeaderboardEntity>,
)