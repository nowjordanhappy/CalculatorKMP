package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation

import com.nowjordanhappy.calculatorkmp.core.domain.CalculatorError

data class CalculatorState(
    val expression: String = "",
    val result: String = "",
    val error: CalculatorError? = null,
    val isRad: Boolean = false,
    val isAcMode: Boolean = true,
)
