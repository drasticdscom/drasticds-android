package com.drasticds.emulator.ui.cheats.model

import com.drasticds.emulator.domain.model.Cheat
import com.drasticds.emulator.domain.model.CheatFolder

data class DeletedCheat(val cheat: Cheat, val folder: CheatFolder)