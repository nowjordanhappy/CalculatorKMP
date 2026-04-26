package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.ButtonLabels
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.CalculatorAction

private val scientificRows =
    listOf(
        listOf(
            ButtonLabels.Scientific.SIN,
            ButtonLabels.Scientific.COS,
            ButtonLabels.Scientific.TAN,
            ButtonLabels.Scientific.RECIPROCAL,
        ),
        listOf(
            ButtonLabels.Scientific.ASIN,
            ButtonLabels.Scientific.ACOS,
            ButtonLabels.Scientific.ATAN,
            ButtonLabels.Scientific.POWER,
        ),
        listOf(
            ButtonLabels.Scientific.LN,
            ButtonLabels.Scientific.LOG,
            ButtonLabels.Scientific.SQRT,
            ButtonLabels.Scientific.SQUARE,
        ),
        listOf(
            ButtonLabels.Scientific.PI,
            ButtonLabels.Scientific.E,
            ButtonLabels.Scientific.OPEN_PAREN,
            ButtonLabels.Scientific.CLOSE_PAREN,
        ),
    )

@Composable
fun ScientificButtonGrid(
    isRad: Boolean,
    showDegRad: Boolean = false,
    buttonHeight: Dp? = null,
    degRadHeight: Dp? = null,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rowSpacing = if (buttonHeight != null) 8.dp else 12.dp
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(rowSpacing),
    ) {
        scientificRows.forEach { row ->
            CalcRow {
                row.forEach { label ->
                    CalcButton(label, ButtonType.Scientific, buttonHeight = buttonHeight) {
                        onAction(CalculatorAction.OnScientificFunction(label))
                    }
                }
            }
        }
        if (showDegRad) {
            CalcRow {
                CalcButton(
                    text = if (isRad) ButtonLabels.Scientific.RAD else ButtonLabels.Scientific.DEG,
                    type = ButtonType.Action,
                    buttonHeight = buttonHeight ?: degRadHeight,
                    onClick = { onAction(CalculatorAction.OnDegRadToggle) },
                )
            }
        }
    }
}
