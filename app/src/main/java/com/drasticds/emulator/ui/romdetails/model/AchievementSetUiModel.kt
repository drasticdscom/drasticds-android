package com.drasticds.emulator.ui.romdetails.model

import com.drasticds.emulator.rcheevosapi.model.RAAchievementSet
import java.net.URL

data class AchievementSetUiModel(
    val setId: Long,
    val setTitle: String?,
    val setType: RAAchievementSet.Type,
    val setIcon: URL,
    val setSummary: RomAchievementsSummary,
    val buckets: List<AchievementBucketUiModel>,
)