package com.drasticds.emulator.domain.model.retroachievements

import com.drasticds.emulator.rcheevosapi.model.RAGameId
import java.net.URL

data class RAUserGameData(
    val id: RAGameId,
    val title: String,
    val icon: URL,
    val richPresencePatch: String?,
    val sets: List<RAUserAchievementSet>,
)