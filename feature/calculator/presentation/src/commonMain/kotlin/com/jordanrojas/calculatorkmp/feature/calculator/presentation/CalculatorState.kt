package com.jordanrojas.calculatorkmp.feature.calculator.presentation

import com.jordanrojas.calculatorkmp.core.domain.CalculatorError

data class CalculatorState(
    val expression: String = "",
    val result: String = "",
    val error: CalculatorError? = null
)
