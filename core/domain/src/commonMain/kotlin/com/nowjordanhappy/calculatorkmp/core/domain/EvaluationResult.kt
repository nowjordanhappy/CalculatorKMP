package com.nowjordanhappy.calculatorkmp.core.domain

sealed interface EvaluationResult {
    data class Success(val value: Double) : EvaluationResult

    data class Error(val error: CalculatorError) : EvaluationResult

    data object NoOp : EvaluationResult
}
