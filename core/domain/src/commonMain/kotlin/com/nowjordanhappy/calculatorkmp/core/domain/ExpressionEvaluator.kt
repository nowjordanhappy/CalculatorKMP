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
        val endsWithOperator =
            lastChar == Constants.OPERATOR_MULTI ||
                lastChar == Constants.OPERATOR_DIV ||
                lastChar == Constants.OPERATOR_SUM ||
                lastChar == Constants.OPERATOR_SUB ||
                lastChar == Constants.OPERATOR_POWER
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
                ch == Constants.OPERATOR_MULTI ||
                    ch == Constants.OPERATOR_DIV ||
                    ch == Constants.OPERATOR_SUM ||
                    ch == Constants.OPERATOR_POWER -> {
                    if (i > 0) lastOpIndex = i
                }
                ch == Constants.OPERATOR_SUB -> {
                    if (i > 0) {
                        val prev = expression[i - 1].toString()
                        val prevIsOp =
                            prev == Constants.OPERATOR_MULTI ||
                                prev == Constants.OPERATOR_DIV ||
                                prev == Constants.OPERATOR_SUM ||
                                prev == Constants.OPERATOR_SUB ||
                                prev == Constants.OPERATOR_POWER
                        if (!prevIsOp) lastOpIndex = i
                    }
                }
            }
        }
        return if (lastOpIndex == -1) expression else expression.substring(lastOpIndex + 1)
    }

    private fun hasOperations(expression: String): Boolean {
        val binaryOps =
            setOf(
                Constants.OPERATOR_SUM,
                Constants.OPERATOR_MULTI,
                Constants.OPERATOR_DIV,
                Constants.OPERATOR_POWER,
                "(",
                ")",
            )
        if (expression.any { it.toString() in binaryOps }) return true
        if (expression.any { it.isLetter() || it == 'π' }) return true
        for (i in 1 until expression.length) {
            if (expression[i] == '-') {
                val prev = expression[i - 1].toString()
                val prevIsOp =
                    prev == Constants.OPERATOR_MULTI ||
                        prev == Constants.OPERATOR_DIV ||
                        prev == Constants.OPERATOR_SUM ||
                        prev == Constants.OPERATOR_SUB ||
                        prev == Constants.OPERATOR_POWER ||
                        prev == "("
                if (!prevIsOp) return true
            }
        }
        return false
    }
}
