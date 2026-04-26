package com.nowjordanhappy.calculatorkmp.core.domain

object ExpressionEvaluator {
    fun evaluate(
        operationRef: String,
        isFinal: Boolean,
        isRad: Boolean = true,
    ): EvaluationResult {
        if (operationRef.isEmpty()) return EvaluationResult.NoOp

        var operation = operationRef
        if (operation.endsWith(Constants.POINT)) {
            operation = operation.dropLast(1)
        }

        val lastChar = operation.last().toString()
        val endsWithOperator = lastChar in Constants.BINARY_OPERATORS
        if (endsWithOperator) {
            return if (isFinal) {
                EvaluationResult.Error(CalculatorError.MATH_ERROR)
            } else {
                EvaluationResult.NoOp
            }
        }

        if (isFinal) {
            operation = autoBalanceParens(operation)
        }

        if (!hasOperations(operation)) return EvaluationResult.NoOp

        return try {
            val result = ExpressionParser.evaluate(operation, isRad)
            EvaluationResult.Success(result)
        } catch (e: ArithmeticException) {
            if (isFinal) {
                EvaluationResult.Error(CalculatorError.UNDEFINED)
            } else {
                EvaluationResult.NoOp
            }
        } catch (e: IllegalArgumentException) {
            if (isFinal) {
                EvaluationResult.Error(CalculatorError.MATH_ERROR)
            } else {
                EvaluationResult.NoOp
            }
        }
    }

    private fun autoBalanceParens(expression: String): String {
        var open = 0
        for (ch in expression) {
            when (ch) {
                '(' -> open++
                ')' -> if (open > 0) open--
            }
        }
        return if (open > 0) expression + ")".repeat(open) else expression
    }

    fun lastNumberSegment(expression: String): String {
        var lastOpIndex = -1
        for (i in expression.indices) {
            val ch = expression[i].toString()
            when {
                ch in Constants.BINARY_OPERATORS && ch != Constants.OPERATOR_SUB -> {
                    if (i > 0) lastOpIndex = i
                }
                ch == Constants.OPERATOR_SUB -> {
                    if (i > 0) {
                        val prev = expression[i - 1].toString()
                        if (prev !in Constants.BINARY_OPERATORS) lastOpIndex = i
                    }
                }
            }
        }
        return if (lastOpIndex == -1) expression else expression.substring(lastOpIndex + 1)
    }

    private fun hasOperations(expression: String): Boolean {
        val extendedOps = Constants.BINARY_OPERATORS + setOf("(", ")")
        if (expression.any { it.toString() in extendedOps }) return true
        if (expression.any { it.isLetter() || it == 'π' }) return true
        for (i in 1 until expression.length) {
            if (expression[i] == '-') {
                val prev = expression[i - 1].toString()
                if (prev !in Constants.BINARY_OPERATORS && prev != "(") return true
            }
        }
        return false
    }
}
