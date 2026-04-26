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
}
