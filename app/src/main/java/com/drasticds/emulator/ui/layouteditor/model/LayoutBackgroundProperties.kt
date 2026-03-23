package com.drasticds.emulator.ui.layouteditor.model

import com.drasticds.emulator.domain.model.layout.BackgroundMode
import java.util.UUID

data class LayoutBackgroundProperties(
    val backgroundId: UUID?,
    val backgroundMode: BackgroundMode,
)