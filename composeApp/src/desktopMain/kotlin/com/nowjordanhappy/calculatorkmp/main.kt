package com.nowjordanhappy.calculatorkmp

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.nowjordanhappy.calculatorkmp.di.appModules
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.LayoutConfig
import org.koin.core.context.startKoin

private const val WINDOW_WIDTH_DP = 320
private const val SCIENTIFIC_PANEL_WIDTH_DP = 300
private const val HORIZONTAL_PADDING_DP = 32
private const val BUTTON_SPACING_DP = 12
private const val BUTTON_COLS = 4
private const val BASIC_ROWS = 5
private const val DISPLAY_HEIGHT_DP = 140
private const val TOGGLE_ROW_DP = 48
private const val SPACER_DP = 24
private const val BOTTOM_PADDING_DP = 5

private fun computeLayoutConfig(): LayoutConfig {
    val panelWidth = (WINDOW_WIDTH_DP - HORIZONTAL_PADDING_DP).dp
    val degRadHeight = ((panelWidth.value - (BUTTON_COLS - 1) * BUTTON_SPACING_DP) / BUTTON_COLS).dp
    return LayoutConfig(panelWidth = panelWidth, degRadHeight = degRadHeight)
}

private fun computeWindowSize(isScientific: Boolean): DpSize {
    val availableWidth = WINDOW_WIDTH_DP - HORIZONTAL_PADDING_DP
    val buttonSize = (availableWidth - (BUTTON_COLS - 1) * BUTTON_SPACING_DP) / BUTTON_COLS
    val buttonGridHeight = BASIC_ROWS * buttonSize + (BASIC_ROWS - 1) * BUTTON_SPACING_DP
    val height = (buttonGridHeight + DISPLAY_HEIGHT_DP + TOGGLE_ROW_DP + SPACER_DP + BOTTOM_PADDING_DP).dp
    val width = (WINDOW_WIDTH_DP + if (isScientific) SCIENTIFIC_PANEL_WIDTH_DP else 0).dp
    return DpSize(width, height)
}

fun main() {
    startKoin { modules(appModules) }
    application {
        val windowState = rememberWindowState(size = computeWindowSize(false))
        Window(
            onCloseRequest = ::exitApplication,
            title = "CalculatorKMP",
            state = windowState,
            resizable = false
        ) {
            App(forceWide = true, layoutConfig = computeLayoutConfig(), onIsScientificChanged = { isScientific ->
                windowState.size = computeWindowSize(isScientific)
            })
        }
    }
}
