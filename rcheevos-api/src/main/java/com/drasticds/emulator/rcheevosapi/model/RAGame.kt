package com.drasticds.emulator.rcheevosapi.model

import java.net.URL

data class RAGame(
    val id: RAGameId,
    val title: String,
    val icon: URL,
    val richPresencePatch: String?,
    val sets: List<RAAchievementSet>,
)