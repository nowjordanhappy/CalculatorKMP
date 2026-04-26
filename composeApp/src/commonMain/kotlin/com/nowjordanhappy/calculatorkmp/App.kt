package com.nowjordanhappy.calculatorkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.CalculatorScreenRoot
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.LayoutConfig
import com.nowjordanhappy.calculatorkmp.settings.SettingsRepository
import org.koin.compose.koinInject

@Composable
fun App(onIsScientificChanged: (Boolean) -> Unit = {}, forceWide: Boolean = false, layoutConfig: LayoutConfig? = null) {
    val settingsRepository: SettingsRepository = koinInject()
    val themeMode by settingsRepository.themeMode.collectAsState()
    CalculatorTheme(themeMode = themeMode) {
        CalculatorScreenRoot(
            onIsScientificChanged = onIsScientificChanged,
            forceWide = forceWide,
            layoutConfig = layoutConfig,
            themeMode = themeMode,
            onThemeChange = settingsRepository::setTheme,
        )
    }
}
