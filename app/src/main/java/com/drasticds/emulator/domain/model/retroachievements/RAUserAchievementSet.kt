package com.drasticds.emulator.domain.model.retroachievements

import com.drasticds.emulator.rcheevosapi.model.RAAchievementSet.Type
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import com.drasticds.emulator.rcheevosapi.model.RALeaderboard
import com.drasticds.emulator.rcheevosapi.model.RASetId
import java.net.URL

data class RAUserAchievementSet(
    val id: RASetId,
    val title: String?,
    val type: Type,
    val gameId: RAGameId,
    val iconUrl: URL,
    val achievements: List<RAUserAchievement>,
    val leaderboards: List<RALeaderboard>,
)