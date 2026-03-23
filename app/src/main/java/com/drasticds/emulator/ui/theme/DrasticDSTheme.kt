package com.drasticds.emulator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DrasticDSTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) DarkDrasticDSColors else LightDrasticDSColors

    MaterialTheme(
        colors = colors,
        typography = DrasticDSTypography,
    ) {
        content()
    }
}