package com.jordanrojas.calculatorkmp.feature.calculator.presentation

sealed interface CalculatorAction {
    data class OnNumberClick(val number: String) : CalculatorAction
    data class OnOperatorClick(val operator: String) : CalculatorAction
    data object OnPointClick : CalculatorAction
    data object OnDeleteClick : CalculatorAction
    data object OnClearClick : CalculatorAction
    data object OnResolveClick : CalculatorAction
    data object OnPercentClick : CalculatorAction
    data object OnSignToggleClick : CalculatorAction
    data object OnErrorDismiss : CalculatorAction
    data object OnScientificToggle : CalculatorAction
    data object OnDegRadToggle : CalculatorAction
    data class OnScientificFunction(val function: String) : CalculatorAction
}
