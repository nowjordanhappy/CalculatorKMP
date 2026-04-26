package com.nowjordanhappy.calculatorkmp.core.domain

object Constants {
    const val OPERATOR_MULTI = "x"
    const val OPERATOR_DIV = "÷"
    const val OPERATOR_SUB = "-"
    const val OPERATOR_SUM = "+"
    const val OPERATOR_POWER = "^"
    const val OPERATOR_NULL = "null"
    const val POINT = "."

    val BINARY_OPERATORS = setOf(OPERATOR_MULTI, OPERATOR_DIV, OPERATOR_SUM, OPERATOR_SUB, OPERATOR_POWER)
}
