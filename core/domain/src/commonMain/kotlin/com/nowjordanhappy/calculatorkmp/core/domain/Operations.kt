package com.nowjordanhappy.calculatorkmp.core.domain

object Operations {

    fun tryResolve(operationRef: String, isFromResolve: Boolean): OperationResult {
        if (operationRef.isEmpty()) return OperationResult.NoOp

        var operation = operationRef
        if (operation.endsWith(Constants.POINT)) {
            operation = operation.dropLast(1)
        }

        val endsWithOperator = operation.last().toString().let {
            it == Constants.OPERATOR_MULTI || it == Constants.OPERATOR_DIV ||
            it == Constants.OPERATOR_SUM || it == Constants.OPERATOR_SUB
        }
        if (endsWithOperator) {
            return if (isFromResolve) OperationResult.Error(CalculatorError.INCORRECT_EXPRESSION, true)
            else OperationResult.NoOp
        }

        val tokens = tokenize(operation)
            ?: return if (isFromResolve) OperationResult.Error(CalculatorError.INCORRECT_NUMBER, true)
            else OperationResult.NoOp

        if (tokens.operators.isEmpty()) return OperationResult.NoOp

        return try {
            val result = evaluate(tokens)
            OperationResult.Success(result, isFromResolve)
        } catch (e: ArithmeticException) {
            if (isFromResolve) OperationResult.Error(CalculatorError.INCORRECT_NUMBER, true)
            else OperationResult.NoOp
        }
    }

    fun canReplaceOperator(charSequence: String): Boolean {
        if (charSequence.length < 2) return false
        val last = charSequence[charSequence.length - 1].toString()
        val prev = charSequence[charSequence.length - 2].toString()
        return (last == Constants.OPERATOR_MULTI || last == Constants.OPERATOR_DIV || last == Constants.OPERATOR_SUM) &&
                (prev == Constants.OPERATOR_MULTI || prev == Constants.OPERATOR_DIV ||
                        prev == Constants.OPERATOR_SUM || prev == Constants.OPERATOR_SUB)
    }

    fun lastNumberSegment(expression: String): String {
        var lastOpIndex = -1
        for (i in expression.indices) {
            val ch = expression[i].toString()
            when {
                ch == Constants.OPERATOR_MULTI || ch == Constants.OPERATOR_DIV || ch == Constants.OPERATOR_SUM -> {
                    if (i > 0) lastOpIndex = i
                }
                ch == Constants.OPERATOR_SUB -> {
                    // Only an operator if the previous char is NOT an operator (otherwise it's a negative sign)
                    if (i > 0) {
                        val prev = expression[i - 1].toString()
                        val prevIsOp = prev == Constants.OPERATOR_MULTI || prev == Constants.OPERATOR_DIV ||
                                prev == Constants.OPERATOR_SUM || prev == Constants.OPERATOR_SUB
                        if (!prevIsOp) lastOpIndex = i
                    }
                }
            }
        }
        return if (lastOpIndex == -1) expression else expression.substring(lastOpIndex + 1)
    }

    private fun tokenize(expression: String): Tokens? {
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<String>()
        val current = StringBuilder()

        for (i in expression.indices) {
            val ch = expression[i].toString()
            val isNonMinusOp = ch == Constants.OPERATOR_MULTI ||
                    ch == Constants.OPERATOR_DIV ||
                    ch == Constants.OPERATOR_SUM

            when {
                isNonMinusOp -> {
                    if (current.isEmpty()) return null
                    numbers.add(current.toString().toDoubleOrNull() ?: return null)
                    operators.add(ch)
                    current.clear()
                }
                ch == Constants.OPERATOR_SUB -> {
                    if (current.isEmpty()) {
                        current.append(ch)
                    } else {
                        numbers.add(current.toString().toDoubleOrNull() ?: return null)
                        operators.add(ch)
                        current.clear()
                    }
                }
                else -> current.append(ch)
            }
        }

        if (current.isEmpty() || current.toString() == Constants.OPERATOR_SUB) return null
        numbers.add(current.toString().toDoubleOrNull() ?: return null)

        return Tokens(numbers.toMutableList(), operators.toMutableList())
    }

    private fun evaluate(tokens: Tokens): Double {
        val numbers = tokens.numbers.toMutableList()
        val operators = tokens.operators.toMutableList()

        var i = 0
        while (i < operators.size) {
            if (operators[i] == Constants.OPERATOR_MULTI || operators[i] == Constants.OPERATOR_DIV) {
                val result = applyOp(numbers[i], operators[i], numbers[i + 1])
                numbers[i] = result
                numbers.removeAt(i + 1)
                operators.removeAt(i)
            } else {
                i++
            }
        }

        var result = numbers[0]
        for (j in operators.indices) {
            result = applyOp(result, operators[j], numbers[j + 1])
        }
        return result
    }

    private fun applyOp(a: Double, operator: String, b: Double): Double = when (operator) {
        Constants.OPERATOR_MULTI -> a * b
        Constants.OPERATOR_DIV -> {
            if (b == 0.0) throw ArithmeticException("Division by zero")
            a / b
        }
        Constants.OPERATOR_SUM -> a + b
        else -> a - b
    }

    private data class Tokens(val numbers: MutableList<Double>, val operators: MutableList<String>)
}
