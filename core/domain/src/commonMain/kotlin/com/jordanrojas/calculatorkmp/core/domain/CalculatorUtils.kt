package com.jordanrojas.calculatorkmp.core.domain

class CalculatorUtils(private val operations: Operations = Operations) {

    fun checkOrResolve(operation: String, isFromResolve: Boolean): OperationResult =
        operations.tryResolve(operation, isFromResolve)

    fun addPoint(operation: String): Boolean {
        val lastSegment = operations.lastNumberSegment(operation)
        return !lastSegment.contains(Constants.POINT)
    }
}
