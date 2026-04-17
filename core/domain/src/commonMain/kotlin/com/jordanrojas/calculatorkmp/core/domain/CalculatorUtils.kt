package com.jordanrojas.calculatorkmp.core.domain

class CalculatorUtils(private val operations: Operations = Operations) {

    fun checkOrResolve(operation: String, isFromResolve: Boolean): OperationResult =
        operations.tryResolve(operation, isFromResolve)

    fun addOperator(operator: String, operation: String): Boolean {
        val last = if (operation.isEmpty()) "" else operation.last().toString()
        return if (operator == Constants.OPERATOR_SUB) {
            operation.isEmpty() || (last != Constants.OPERATOR_SUB && last != Constants.POINT)
        } else {
            operation.isNotEmpty() && last != Constants.POINT
        }
    }

    fun addPoint(operation: String): Boolean {
        val lastSegment = operations.lastNumberSegment(operation)
        return !lastSegment.contains(Constants.POINT)
    }
}
