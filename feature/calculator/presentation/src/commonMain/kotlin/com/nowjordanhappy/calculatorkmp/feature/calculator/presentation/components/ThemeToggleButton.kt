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
import androidx.compose.ui.graphics.vector.ImageVector
import com.nowjordanhappy.calculatorkmp.core.domain.ThemeMode
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.LocalStrings

@Composable
fun ThemeToggleButton(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    val icon: ImageVector =
        when (themeMode) {
            ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
            ThemeMode.LIGHT -> Icons.Default.LightMode
            ThemeMode.DARK -> Icons.Default.DarkMode
        }
    val description: String =
        when (themeMode) {
            ThemeMode.SYSTEM -> strings.themeSystem
            ThemeMode.LIGHT -> strings.themeLight
            ThemeMode.DARK -> strings.themeDark
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
