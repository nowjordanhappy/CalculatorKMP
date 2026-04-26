package com.nowjordanhappy.calculatorkmp.core.domain

class CalculatorUtils(private val operations: Operations = Operations) {
    fun checkOrResolve(
        operation: String,
        isFromResolve: Boolean,
        isRad: Boolean = true,
    ): OperationResult = operations.tryResolve(operation, isFromResolve, isRad)

    fun addPoint(operation: String): Boolean {
        val lastSegment = operations.lastNumberSegment(operation)
        return !lastSegment.contains(Constants.POINT)
    }
}
