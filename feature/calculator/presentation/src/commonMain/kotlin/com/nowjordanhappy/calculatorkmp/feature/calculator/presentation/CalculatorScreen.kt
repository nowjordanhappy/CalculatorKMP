package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components.CalculatorButtonGrid
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components.CalculatorDisplay
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components.ModeMenu
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components.ScientificButtonGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

private val WIDE_BREAKPOINT = 600.dp

data class LayoutConfig(
    val panelWidth: Dp,
    val degRadHeight: Dp
)

@Composable
fun CalculatorScreenRoot(
    onIsScientificChanged: (Boolean) -> Unit = {},
    forceWide: Boolean = false,
    layoutConfig: LayoutConfig? = null,
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isScientific) { onIsScientificChanged(state.isScientific) }
    CalculatorScreen(
        state = state,
        forceWide = forceWide,
        layoutConfig = layoutConfig,
        onAction = viewModel::onAction
    )
}

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    forceWide: Boolean = false,
    layoutConfig: LayoutConfig? = null,
    onAction: (CalculatorAction) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        var stableMaxWidth by remember { mutableStateOf(maxWidth) }
        if (layoutConfig != null) {
            LaunchedEffect(Unit) {
                snapshotFlow { maxWidth }.collectLatest { width ->
                    delay(250)
                    stableMaxWidth = width
                }
            }
        } else {
            SideEffect { stableMaxWidth = maxWidth }
        }

        val isWide = forceWide || stableMaxWidth > maxHeight || stableMaxWidth > WIDE_BREAKPOINT

        val portraitButtonHeight: Dp? = if (state.isScientific && !isWide) {
            val totalSpacings = 64.dp
            val fixedOverhead = 52.dp + 16.dp + 10.dp
            val displayMin = 120.dp
            ((maxHeight - fixedOverhead - displayMin - totalSpacings) / 9).coerceIn(40.dp, 56.dp)
        } else null

        // Capped button height for all wide mobile layouts (not desktop — layoutConfig guards that)
        val wideButtonHeight: Dp? = if (isWide && layoutConfig == null) {
            val rows = 5
            val spacings = 12.dp * (rows - 1)
            val fixedOverhead = 52.dp + 16.dp + 10.dp
            val displayMin = 80.dp
            ((maxHeight - fixedOverhead - displayMin - spacings) / rows).coerceIn(36.dp, 64.dp)
        } else null

        // Desktop uses fixed LayoutConfig. Mobile computes from screen width.
        // Scientific wide: 8 columns (4 sci + 4 basic) + 7 inner gaps + 1 panel gap across available width.
        val availableWidth = stableMaxWidth - 32.dp
        val scientificPanelWidth = layoutConfig?.panelWidth ?: ((availableWidth - 12.dp) / 2)
        val panelWidth = layoutConfig?.panelWidth ?: availableWidth
        val degRadHeight = layoutConfig?.degRadHeight ?: ((scientificPanelWidth - 36.dp) / 4)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ModeMenu(
                isScientific = state.isScientific,
                isRad = state.isRad,
                showDegRad = state.isScientific && !isWide,
                delayToggle = isWide,
                onAction = onAction
            )

            CalculatorDisplay(
                expression = state.expression,
                result = state.result,
                error = state.error,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.height(16.dp))

            if (isWide) {
                WideButtonArea(
                    isScientific = state.isScientific,
                    panelWidth = panelWidth,
                    scientificPanelWidth = scientificPanelWidth,
                    degRadHeight = degRadHeight,
                    isRad = state.isRad,
                    buttonHeight = wideButtonHeight,
                    onAction = onAction
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(if (portraitButtonHeight != null) 8.dp else 12.dp)
                ) {
                    if (state.isScientific) {
                        ScientificButtonGrid(
                            isRad = state.isRad,
                            showDegRad = false,
                            buttonHeight = portraitButtonHeight,
                            onAction = onAction
                        )
                    }
                    CalculatorButtonGrid(buttonHeight = portraitButtonHeight, onAction = onAction)
                }
            }
        }
    }
}

@Composable
private fun WideButtonArea(
    isScientific: Boolean,
    panelWidth: Dp,
    scientificPanelWidth: Dp,
    degRadHeight: Dp,
    isRad: Boolean,
    buttonHeight: Dp?,
    onAction: (CalculatorAction) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (isScientific) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                ScientificButtonGrid(
                    isRad = isRad,
                    showDegRad = true,
                    degRadHeight = degRadHeight,
                    buttonHeight = buttonHeight,
                    onAction = onAction,
                    modifier = Modifier.width(scientificPanelWidth)
                )
            }
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            CalculatorButtonGrid(
                modifier = Modifier.width(if (isScientific) scientificPanelWidth else panelWidth),
                buttonHeight = buttonHeight,
                onAction = onAction
            )
        }
    }
}
