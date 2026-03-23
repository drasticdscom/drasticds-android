package com.drasticds.emulator.domain.model.retroachievements

import com.drasticds.emulator.rcheevosapi.model.RAAchievementSet.Type
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import java.net.URL

data class RAAchievementSetSummary(
    val setId: Long,
    val gameId: RAGameId,
    val title: String?,
    val type: Type,
    val iconUrl: URL,
)