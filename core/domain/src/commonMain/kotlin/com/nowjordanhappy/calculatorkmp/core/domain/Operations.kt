package com.nowjordanhappy.calculatorkmp.core.domain

object Operations {
    fun tryResolve(
        operationRef: String,
        isFromResolve: Boolean,
        isRad: Boolean = true,
    ): OperationResult {
        if (operationRef.isEmpty()) return OperationResult.NoOp

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
            return if (isFromResolve) {
                OperationResult.Error(CalculatorError.MATH_ERROR, true)
            } else {
                OperationResult.NoOp
            }
        }

        if (isFromResolve) {
            operation = autoBalanceParens(operation)
        }

        if (!hasOperations(operation)) return OperationResult.NoOp

        return try {
            val result = ExpressionParser.evaluate(operation, isRad)
            OperationResult.Success(result, isFromResolve)
        } catch (e: ArithmeticException) {
            if (isFromResolve) {
                OperationResult.Error(CalculatorError.UNDEFINED, true)
            } else {
                OperationResult.NoOp
            }
        } catch (e: IllegalArgumentException) {
            if (isFromResolve) {
                OperationResult.Error(CalculatorError.MATH_ERROR, true)
            } else {
                OperationResult.NoOp
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

    fun canReplaceOperator(charSequence: String): Boolean {
        if (charSequence.length < 2) return false
        val last = charSequence[charSequence.length - 1].toString()
        val prev = charSequence[charSequence.length - 2].toString()
        return (last == Constants.OPERATOR_MULTI || last == Constants.OPERATOR_DIV || last == Constants.OPERATOR_SUM) &&
            (prev == Constants.OPERATOR_MULTI ||
                prev == Constants.OPERATOR_DIV ||
                prev == Constants.OPERATOR_SUM ||
                prev == Constants.OPERATOR_SUB)
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
        // '-' at position > 0 and not right after another operator = binary minus
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
