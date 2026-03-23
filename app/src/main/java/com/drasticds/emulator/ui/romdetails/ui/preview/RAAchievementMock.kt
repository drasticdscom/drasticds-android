package com.drasticds.emulator.ui.romdetails.ui.preview

import com.drasticds.emulator.rcheevosapi.model.RAAchievement
import com.drasticds.emulator.rcheevosapi.model.RAGameId
import com.drasticds.emulator.rcheevosapi.model.RASetId
import java.net.URL

fun mockRAAchievementPreview(
    id: Long = 1,
    gameId: Long = 123,
    setId: Long = 1,
    totalAwardsCasual: Int = 5435,
    totalAwardsHardcore: Int = 4532,
    title: String = "Amazing Achievement [m]",
    description: String = "Do the definitely amazing stuff while back-flipping on top of a turtle.",
    points: Int = 10,
    displayOrder: Int = 0,
    badgeUrlUnlocked: String = "http://localhost:80",
    badgeUrlLocked: String = "http://localhost:80",
    memoryAddress: String = "",
    type: RAAchievement.Type = RAAchievement.Type.CORE,
): RAAchievement {
    return RAAchievement(
        id = id,
        gameId = RAGameId(gameId),
        setId = RASetId(setId),
        totalAwardsCasual = totalAwardsCasual,
        totalAwardsHardcore = totalAwardsHardcore,
        title = title,
        description = description,
        points = points,
        displayOrder = displayOrder,
        badgeUrlUnlocked = URL(badgeUrlUnlocked),
        badgeUrlLocked = URL(badgeUrlLocked),
        memoryAddress = memoryAddress,
        type = type,
    )
}