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

    fun applyPercent(expression: String, isRad: Boolean): String? {
        val last = evaluator.lastNumberSegment(expression)
        val value = last.toDoubleOrNull() ?: return null
        val prefix = expression.dropLast(last.length)
        val newLast =
            if (prefix.isNotEmpty()) {
                val lastOp = prefix.last().toString()
                if (lastOp == Constants.OPERATOR_SUM || lastOp == Constants.OPERATOR_SUB) {
                    val baseExpr = prefix.dropLast(1)
                    val baseValue =
                        when (val r = evaluate(baseExpr, false, isRad)) {
                            is EvaluationResult.Success -> r.value
                            else -> baseExpr.toDoubleOrNull()
                        }
                    if (baseValue != null) formatResult(baseValue * value / 100) else formatResult(value / 100)
                } else {
                    formatResult(value / 100)
                }
            } else {
                formatResult(value / 100)
            }
        return prefix + newLast
    }

    fun applySignToggle(expression: String): String? {
        val last = evaluator.lastNumberSegment(expression)
        if (last.isEmpty()) return null
        val newLast = if (last.startsWith("-")) last.drop(1) else "-$last"
        return expression.dropLast(last.length) + newLast
    }

    fun formatResult(value: Double): String {
        if (value == value.toLong().toDouble()) return value.toLong().toString()
        val str = value.toString()
        return if ('E' in str || 'e' in str) plainString(str) else str.trimEnd('0').trimEnd('.')
    }

    private fun plainString(scientific: String): String {
        val eIndex = scientific.indexOfFirst { it == 'E' || it == 'e' }
        val negative = scientific.startsWith("-")
        val base = scientific.substring(if (negative) 1 else 0, eIndex)
        val exp = scientific.substring(eIndex + 1).toInt()
        val digits = base.replace(".", "")
        val dotPos = base.indexOf('.').let { if (it == -1) base.length else it }
        val newDotPos = dotPos + exp
        val plain =
            when {
                newDotPos <= 0 -> "0." + "0".repeat(-newDotPos) + digits
                newDotPos >= digits.length -> digits + "0".repeat(newDotPos - digits.length)
                else -> digits.substring(0, newDotPos) + "." + digits.substring(newDotPos)
            }
        return ((if (negative) "-" else "") + plain).trimEnd('0').trimEnd('.')
    }
}
