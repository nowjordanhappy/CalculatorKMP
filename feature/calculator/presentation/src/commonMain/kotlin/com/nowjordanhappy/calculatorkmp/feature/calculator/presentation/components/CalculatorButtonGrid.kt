package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nowjordanhappy.calculatorkmp.core.domain.Constants
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.ButtonLabels
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.CalculatorAction
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.LocalStrings

@Composable
fun CalculatorButtonGrid(
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
    buttonHeight: Dp? = null,
    isAcMode: Boolean = true,
) {
    val strings = LocalStrings.current
    val rowSpacing = if (buttonHeight != null) 8.dp else 12.dp
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(rowSpacing)) {
        CalcRow {
            CalcButton(
                if (isAcMode) ButtonLabels.Basic.AC else ButtonLabels.Basic.CLEAR,
                ButtonType.Action,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonClear,
            ) {
                onAction(CalculatorAction.OnClearClick)
            }
            CalcButton(
                ButtonLabels.Basic.SIGN_TOGGLE,
                ButtonType.Action,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonToggleSign,
            ) {
                onAction(CalculatorAction.OnSignToggleClick)
            }
            CalcButton(
                ButtonLabels.Basic.PERCENT,
                ButtonType.Action,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonPercent,
            ) {
                onAction(CalculatorAction.OnPercentClick)
            }
            CalcButton(Constants.OPERATOR_DIV, ButtonType.Operator, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
            }
        }
        CalcRow {
            CalcButton("7", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("7")) }
            CalcButton("8", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("8")) }
            CalcButton("9", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("9")) }
            CalcButton(Constants.OPERATOR_MULTI, ButtonType.Operator, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_MULTI))
            }
        }
        CalcRow {
            CalcButton("4", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("4")) }
            CalcButton("5", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("5")) }
            CalcButton("6", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("6")) }
            CalcButton(Constants.OPERATOR_SUB, ButtonType.Operator, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_SUB))
            }
        }
        CalcRow {
            CalcButton("1", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("1")) }
            CalcButton("2", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("2")) }
            CalcButton("3", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("3")) }
            CalcButton(Constants.OPERATOR_SUM, ButtonType.Operator, buttonHeight = buttonHeight) {
                onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_SUM))
            }
        }
        CalcRow {
            CalcButton(
                ButtonLabels.Basic.DELETE,
                ButtonType.Action,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonDelete,
            ) {
                onAction(CalculatorAction.OnDeleteClick)
            }
            CalcButton("0", buttonHeight = buttonHeight) { onAction(CalculatorAction.OnNumberClick("0")) }
            CalcButton(Constants.POINT, buttonHeight = buttonHeight) { onAction(CalculatorAction.OnPointClick) }
            CalcButton(
                ButtonLabels.Basic.EQUALS,
                ButtonType.Equals,
                buttonHeight = buttonHeight,
                contentDescription = strings.buttonEquals,
            ) {
                onAction(CalculatorAction.OnResolveClick)
            }
        }
    }
}
