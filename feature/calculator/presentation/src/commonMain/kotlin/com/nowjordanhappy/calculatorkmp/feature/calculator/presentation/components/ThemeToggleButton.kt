package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nowjordanhappy.calculatorkmp.core.domain.ThemeMode

@Composable
fun ThemeToggleButton(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, description) =
        when (themeMode) {
            ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto to "System theme"
            ThemeMode.LIGHT -> Icons.Default.LightMode to "Light theme"
            ThemeMode.DARK -> Icons.Default.DarkMode to "Dark theme"
        }
    IconButton(
        onClick = {
            onThemeChange(
                when (themeMode) {
                    ThemeMode.SYSTEM -> ThemeMode.LIGHT
                    ThemeMode.LIGHT -> ThemeMode.DARK
                    ThemeMode.DARK -> ThemeMode.SYSTEM
                }
            )
        },
        modifier = modifier,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}
