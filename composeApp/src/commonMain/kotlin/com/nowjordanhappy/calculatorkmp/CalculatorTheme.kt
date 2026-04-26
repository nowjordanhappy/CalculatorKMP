package com.nowjordanhappy.calculatorkmp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Orange = Color(0xFFFF9500)
val OrangeLight = Color(0xFFFFB340)

private val DarkColors =
    darkColorScheme(
        background = Color(0xFF1C1C1E),
        surface = Color(0xFF1C1C1E),
        primary = Orange,
        onPrimary = Color.White,
        secondary = Color(0xFF505052),
        onSecondary = Color.White,
        tertiary = Color(0xFF2C2C2E),
        onTertiary = Color.White,
        surfaceVariant = Color(0xFF3A3A3C),
        onSurfaceVariant = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
    )

private val LightColors =
    lightColorScheme(
        background = Color(0xFFF2F2F7),
        surface = Color(0xFFF2F2F7),
        primary = Orange,
        onPrimary = Color.White,
        secondary = Color(0xFFE5E5EA),
        onSecondary = Color(0xFF1C1C1E),
        tertiary = Color(0xFFFFFFFF),
        onTertiary = Color(0xFF1C1C1E),
        surfaceVariant = Color(0xFFD1D1D6),
        onSurfaceVariant = Color(0xFF1C1C1E),
        onBackground = Color(0xFF1C1C1E),
        onSurface = Color(0xFF1C1C1E),
    )

@Composable
fun CalculatorTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}
