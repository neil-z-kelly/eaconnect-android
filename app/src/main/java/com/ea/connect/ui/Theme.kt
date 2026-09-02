package com.ea.connect.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object EaColors {
    val Midnight = Color(0xFF060B1F)
    val Navy = Color(0xFF0B1330)
    val Surface = Color(0xFF121C3D)
    val SurfaceHigh = Color(0xFF1B2752)
    val Outline = Color(0xFF2A3766)
    val Blue = Color(0xFF3D6BFF)
    val BlueDeep = Color(0xFF1F3FD1)
    val White = Color(0xFFFFFFFF)
    val Muted = Color(0xFF9AA6CC)
    val Online = Color(0xFF35D06B)
    val Busy = Color(0xFFE4453A)
    val Away = Color(0xFFF2B33D)
    val Error = Color(0xFFB3261E)
}

val EaBackdrop = Brush.verticalGradient(
    listOf(EaColors.Midnight, EaColors.Navy, Color(0xFF101B45)),
)

private val Scheme = darkColorScheme(
    primary = EaColors.Blue,
    onPrimary = EaColors.White,
    secondary = EaColors.BlueDeep,
    onSecondary = EaColors.White,
    background = EaColors.Midnight,
    onBackground = EaColors.White,
    surface = EaColors.Surface,
    onSurface = EaColors.White,
    surfaceVariant = EaColors.SurfaceHigh,
    onSurfaceVariant = EaColors.Muted,
    outline = EaColors.Outline,
    error = EaColors.Error,
)

private val EaTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
    ),
)

@Composable
fun EaConnectTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = EaTypography, content = content)
}
