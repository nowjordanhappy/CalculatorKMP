package com.nowjordanhappy.calculatorkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.CalculatorScreenRoot
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.LayoutConfig
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.LocalStrings
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.getStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(forceWide: Boolean = false, layoutConfig: LayoutConfig? = null) {
    val appViewModel: AppViewModel = koinViewModel()
    val state by appViewModel.state.collectAsState()
    val locale = Locale.current.toLanguageTag()
    val strings = remember(locale) { getStrings(locale) }
    CalculatorTheme(themeMode = state.themeMode) {
        CompositionLocalProvider(LocalStrings provides strings) {
            CalculatorScreenRoot(
                isScientific = state.isScientific,
                onScientificToggle = { appViewModel.onAction(AppAction.OnScientificToggle) },
                themeMode = state.themeMode,
                onThemeChange = { appViewModel.onAction(AppAction.OnThemeChange(it)) },
                forceWide = forceWide,
                layoutConfig = layoutConfig,
            )
        }
    }
}
