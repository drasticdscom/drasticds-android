package com.drasticds.emulator.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.drasticds.emulator.R

val uncheckedThumbColor: Color @Composable get() = colorResource(id = R.color.switchThumbUnselected)
val gameMasteryColor: Color get() = Color(0xFFFFD700)

val Primary         = Color(0xFF38629F)   // drasticds.com accent — PRIMARY
val PrimaryDark     = Color(0xFF1E3A5F)   // Darker shade for dark theme
val PrimaryLight    = Color(0xFF5B8ACC)   // Lighter shade for highlights
val Background      = Color(0xFF0F172A)   // Deep dark — matches site dark mode
val Surface         = Color(0xFF1E293B)   // Card surfaces
val SurfaceVariant  = Color(0xFF334155)   // Secondary surfaces
val OnPrimary       = Color(0xFFFFFFFF)   // White text on primary
val OnBackground    = Color(0xFFE2E8F0)   // Light text on dark background
val OnSurface       = Color(0xFFCBD5E1)   // Secondary text
val Accent          = Color(0xFF38629F)   // Same as primary
val Success         = Color(0xFF16A34A)   // Save state success, positive states
val ErrorCustom     = Color(0xFFDC2626)   // Error states

val LightDrasticDSColors @Composable get() = lightColors(
    primary = Primary,
    primaryVariant = PrimaryDark,
    secondary = Accent,
    secondaryVariant = Accent,
    background = Color(0xFFF8FAFC), // Light Theme: Background #F8FAFC
    surface = Color(0xFFFFFFFF),    // Light Theme: Surface #FFFFFF
    onPrimary = OnPrimary,
    onSecondary = OnPrimary,
    onSurface = Color(0xFF0F172A),  // Light Theme: Text #0F172A
    onBackground = Color(0xFF0F172A),
    error = ErrorCustom
)

val DarkDrasticDSColors @Composable get() = darkColors(
    primary = Primary,
    primaryVariant = PrimaryDark,
    secondary = Accent,
    secondaryVariant = Accent,
    background = Background,        // Dark Theme: Background #0F172A
    surface = Surface,              // Dark Theme: Surface #1E293B
    onPrimary = OnPrimary,
    onSecondary = OnPrimary,
    onSurface = OnSurface,          // Dark Theme: Text #CBD5E1
    onBackground = OnBackground,
    error = ErrorCustom
)