package com.nowjordanhappy.calculatorkmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.CalculatorScreenRoot
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.LayoutConfig
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(forceWide: Boolean = false, layoutConfig: LayoutConfig? = null) {
    val appViewModel: AppViewModel = koinViewModel()
    val state by appViewModel.state.collectAsState()
    CalculatorTheme(themeMode = state.themeMode) {
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
