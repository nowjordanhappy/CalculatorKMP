package com.jordanrojas.calculatorkmp.feature.calculator.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.CalculatorAction

private val scientificRows = listOf(
    listOf("sin", "cos", "tan", "1/x"),
    listOf("sin⁻¹", "cos⁻¹", "tan⁻¹", "xʸ"),
    listOf("ln", "log", "√x", "x²"),
    listOf("π", "e", "(", ")")
)

@Composable
fun ScientificButtonGrid(
    isRad: Boolean,
    showDegRad: Boolean = false,
    buttonHeight: Dp? = null,
    degRadHeight: Dp? = null,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val rowSpacing = if (buttonHeight != null) 8.dp else 12.dp
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(rowSpacing)
    ) {
        scientificRows.forEach { row ->
            CalcRow {
                row.forEach { label ->
                    CalcButton(label, ButtonType.Action, buttonHeight = buttonHeight) {
                        onAction(CalculatorAction.OnScientificFunction(label))
                    }
                }
            }
        }
        if (showDegRad) {
            CalcRow {
                CalcButton(
                    text = if (isRad) "RAD" else "DEG",
                    type = ButtonType.Action,
                    buttonHeight = degRadHeight ?: buttonHeight,
                    onClick = { onAction(CalculatorAction.OnDegRadToggle) }
                )
            }
        }
    }
}
