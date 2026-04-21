package com.nowjordanhappy.calculatorkmp.core.domain

sealed interface OperationResult {
    data class Success(val value: Double, val isFromResolve: Boolean) : OperationResult
    data class Error(val error: CalculatorError, val isFromResolve: Boolean) : OperationResult
    data object NoOp : OperationResult
}
