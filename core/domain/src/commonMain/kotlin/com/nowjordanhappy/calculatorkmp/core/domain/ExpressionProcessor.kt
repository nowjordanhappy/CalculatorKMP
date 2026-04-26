package com.nowjordanhappy.calculatorkmp.core.domain

class ExpressionProcessor(private val evaluator: ExpressionEvaluator = ExpressionEvaluator) {
    fun evaluate(
        expression: String,
        isFinal: Boolean,
        isRad: Boolean = true,
    ): EvaluationResult = evaluator.evaluate(expression, isFinal, isRad)

    fun addPoint(expression: String): Boolean {
        val lastSegment = evaluator.lastNumberSegment(expression)
        return !lastSegment.contains(Constants.POINT)
    }

    fun appendDigit(current: String, digit: String): String =
        when {
            current == "0" && digit != "0" -> digit
            current == "0" && digit == "0" -> current
            current.isNotEmpty() && current.last().toString() in Constants.BINARY_OPERATORS && digit == "0" ->
                current + digit
            current.length >= 2 &&
                current.last().toString() == "0" &&
                current[current.length - 2].toString() in Constants.BINARY_OPERATORS &&
                digit == "0" -> current
            current.length >= 2 &&
                current.last().toString() == "0" &&
                current[current.length - 2].toString() in Constants.BINARY_OPERATORS &&
                digit != "0" -> current.dropLast(1) + digit
            current.isNotEmpty() && (current.last() == ')' || current.last() == 'π' || current.last() == 'e') ->
                current + Constants.OPERATOR_MULTI + digit
            else -> current + digit
        }

    fun needsImplicitMultiply(expr: String): Boolean {
        if (expr.isEmpty()) return false
        val last = expr.last()
        return last.isDigit() || last == '.' || last == ')' || last == 'π' || last == 'e'
    }
}
