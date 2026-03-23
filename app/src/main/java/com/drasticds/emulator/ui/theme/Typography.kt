package com.drasticds.emulator.ui.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.drasticds.emulator.R

val Manrope = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold)
)

val DrasticDSTypography @Composable get() = Typography(
    defaultFontFamily = Manrope,
    body1 = Typography().body1.copy(lineHeight = 20.sp),
    button = Typography().button.copy(fontWeight = FontWeight.Bold),
)