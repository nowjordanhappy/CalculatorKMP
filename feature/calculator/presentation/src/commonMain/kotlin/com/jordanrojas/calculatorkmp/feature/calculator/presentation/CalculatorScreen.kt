package com.jordanrojas.calculatorkmp.feature.calculator.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.movableContentOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.components.CalculatorButtonGrid
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.components.CalculatorDisplay
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.components.ModeMenu
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.components.ScientificButtonGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

private val WIDE_BREAKPOINT = 600.dp

@Composable
fun CalculatorScreenRoot(
    onIsScientificChanged: (Boolean) -> Unit = {},
    forceWide: Boolean = false,
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.isScientific) { onIsScientificChanged(state.isScientific) }
    CalculatorScreen(state = state, forceWide = forceWide, onAction = viewModel::onAction)
}

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    forceWide: Boolean = false,
    onAction: (CalculatorAction) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        var stableMaxWidth by remember { mutableStateOf(maxWidth) }
        LaunchedEffect(Unit) {
            snapshotFlow { maxWidth }.collectLatest { width ->
                //delay(250)
                stableMaxWidth = width
            }
        }

        val isWide = state.isScientific && (forceWide || stableMaxWidth > WIDE_BREAKPOINT)

        var basicModeWidth by remember { mutableStateOf(maxWidth) }
        LaunchedEffect(state.isScientific) { if (state.isScientific) basicModeWidth = maxWidth }

        val portraitButtonHeight: Dp? = if (state.isScientific && !isWide) {
            val totalSpacings = 64.dp
            val fixedOverhead = 52.dp + 16.dp + 10.dp
            val displayMin = 120.dp
            ((maxHeight - fixedOverhead - displayMin - totalSpacings) / 9).coerceIn(40.dp, 56.dp)
        } else null

        val panelWidth = basicModeWidth - 32.dp
        val degRadHeight = (panelWidth - 36.dp) / 4

        val basicGrid = remember {
            movableContentOf {
                CalculatorButtonGrid(
                    modifier = if (isWide) Modifier.width(panelWidth) else Modifier,
                    onAction = onAction
                )
            }
        }

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
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (state.isScientific) {
                        ScientificButtonGrid(
                            isRad = state.isRad,
                            showDegRad = true,
                            degRadHeight = degRadHeight,
                            onAction = onAction,
                            modifier = Modifier.width(panelWidth)
                        )
                    }
                    basicGrid()
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(if (portraitButtonHeight != null) 8.dp else 12.dp)) {
                    if (state.isScientific) {
                        ScientificButtonGrid(
                            isRad = state.isRad,
                            showDegRad = false,
                            buttonHeight = portraitButtonHeight,
                            onAction = onAction
                        )
                    }
                    basicGrid()
                }
            }
        }
    }
}
