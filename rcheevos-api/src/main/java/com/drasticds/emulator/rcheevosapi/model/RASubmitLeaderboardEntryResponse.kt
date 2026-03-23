package com.drasticds.emulator.rcheevosapi.model

data class RASubmitLeaderboardEntryResponse(
    val gameId: RAGameId,
    val title: String,
    val formattedScore: String,
    val rank: Int,
    val numEntries: Int,
)