package com.drasticds.emulator.ui.emulator.components

import androidx.compose.ui.graphics.Color

data class ControllerSkin(
    val name: String,
    val buttonColor: Color,
    val buttonPressedColor: Color,
    val textColor: Color,
    val dpadColor: Color,
    val accentColor: Color
)

val ClassicSkin = ControllerSkin(
    name = "Classic",
    buttonColor = Color(0xFF4A4A4A),
    buttonPressedColor = Color(0xFF6A6A6A),
    textColor = Color.White,
    dpadColor = Color(0xFF333333),
    accentColor = Color(0xFF38629F)
)

val NeonSkin = ControllerSkin(
    name = "Neon",
    buttonColor = Color(0xFF1E293B),
    buttonPressedColor = Color(0xFF334155),
    textColor = Color(0xFF60A5FA),
    dpadColor = Color(0xFF0F172A),
    accentColor = Color(0xFF3B82F6)
)
