package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.ButtonLabels
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.CalculatorAction
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.LocalStrings

@Composable
fun ScientificButtonGrid(
    isRad: Boolean,
    showDegRad: Boolean = false,
    buttonHeight: Dp? = null,
    degRadHeight: Dp? = null,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    val rowSpacing = if (buttonHeight != null) 8.dp else 12.dp
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(rowSpacing),
    ) {
        CalcRow {
            CalcButton(ButtonLabels.Scientific.SIN, ButtonType.Scientific, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.SIN))
            }
            CalcButton(ButtonLabels.Scientific.COS, ButtonType.Scientific, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.COS))
            }
            CalcButton(ButtonLabels.Scientific.TAN, ButtonType.Scientific, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.TAN))
            }
            CalcButton(
                ButtonLabels.Scientific.RECIPROCAL,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonReciprocal,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.RECIPROCAL))
            }
        }
        CalcRow {
            CalcButton(
                ButtonLabels.Scientific.ASIN,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonInverseSin,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.ASIN))
            }
            CalcButton(
                ButtonLabels.Scientific.ACOS,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonInverseCos,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.ACOS))
            }
            CalcButton(
                ButtonLabels.Scientific.ATAN,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonInverseTan,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.ATAN))
            }
            CalcButton(
                ButtonLabels.Scientific.POWER,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonPower,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.POWER))
            }
        }
        CalcRow {
            CalcButton(ButtonLabels.Scientific.LN, ButtonType.Scientific, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.LN))
            }
            CalcButton(ButtonLabels.Scientific.LOG, ButtonType.Scientific, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.LOG))
            }
            CalcButton(
                ButtonLabels.Scientific.SQRT,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonSquareRoot,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.SQRT))
            }
            CalcButton(
                ButtonLabels.Scientific.SQUARE,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonSquare,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.SQUARE))
            }
        }
        CalcRow {
            CalcButton(
                ButtonLabels.Scientific.PI,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonPi,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.PI))
            }
            CalcButton(
                ButtonLabels.Scientific.E,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonE,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.E))
            }
            CalcButton(
                ButtonLabels.Scientific.OPEN_PAREN,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonOpenParen,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.OPEN_PAREN))
            }
            CalcButton(
                ButtonLabels.Scientific.CLOSE_PAREN,
                ButtonType.Scientific,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonCloseParen,
            ) {
                onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.CLOSE_PAREN))
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
