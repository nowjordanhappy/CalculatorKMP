package com.nowjordanhappy.calculatorkmp.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExpressionEvaluatorTest {
    // evaluate — single operator

    @Test
    fun evaluate_multiplication_returnsSuccess() {
        val result = ExpressionEvaluator.evaluate("5x2", true)
        assertTrue(result is EvaluationResult.Success)
        assertEquals(10.0, (result as EvaluationResult.Success).value)
    }

    @Test
    fun evaluate_division_returnsSuccess() {
        val result = ExpressionEvaluator.evaluate("10÷2", true)
        assertTrue(result is EvaluationResult.Success)
        assertEquals(5.0, (result as EvaluationResult.Success).value)
    }

    @Test
    fun evaluate_sum_returnsSuccess() {
        val result = ExpressionEvaluator.evaluate("3+4", true)
        assertTrue(result is EvaluationResult.Success)
        assertEquals(7.0, (result as EvaluationResult.Success).value)
    }

    @Test
    fun evaluate_subtraction_returnsSuccess() {
        val result = ExpressionEvaluator.evaluate("10-3", true)
        assertTrue(result is EvaluationResult.Success)
        assertEquals(7.0, (result as EvaluationResult.Success).value)
    }

    // evaluate — multiple operators with precedence

    @Test
    fun evaluate_multipleOperators_respectsPrecedence() {
        val result = ExpressionEvaluator.evaluate("856x8+9", true)
        assertTrue(result is EvaluationResult.Success)
        assertEquals(6857.0, (result as EvaluationResult.Success).value)
    }

    @Test
    fun evaluate_addThenMultiply_respectsPrecedence() {
        val result = ExpressionEvaluator.evaluate("2+3x4", true)
        assertTrue(result is EvaluationResult.Success)
        assertEquals(14.0, (result as EvaluationResult.Success).value)
    }

    @Test
    fun evaluate_multipleAdditions_leftToRight() {
        val result = ExpressionEvaluator.evaluate("1+2+3", true)
        assertTrue(result is EvaluationResult.Success)
        assertEquals(6.0, (result as EvaluationResult.Success).value)
    }

    @Test
    fun evaluate_negativeSecondOperand_works() {
        val result = ExpressionEvaluator.evaluate("5+-3", true)
        assertTrue(result is EvaluationResult.Success)
        assertEquals(2.0, (result as EvaluationResult.Success).value)
    }

    // evaluate — edge cases

    @Test
    fun evaluate_emptyExpression_returnsNoOp() {
        assertEquals(EvaluationResult.NoOp, ExpressionEvaluator.evaluate("", true))
    }

    @Test
    fun evaluate_incompleteExpression_isFinal_returnsError() {
        val result = ExpressionEvaluator.evaluate("5+", true)
        assertTrue(result is EvaluationResult.Error)
        assertEquals(CalculatorError.MATH_ERROR, (result as EvaluationResult.Error).error)
    }

    @Test
    fun evaluate_trailingPoint_stripsPointAndReturnsNoOp() {
        assertEquals(EvaluationResult.NoOp, ExpressionEvaluator.evaluate("5.", true))
    }

    @Test
    fun evaluate_divisionByZero_returnsError() {
        val result = ExpressionEvaluator.evaluate("5÷0", true)
        assertTrue(result is EvaluationResult.Error)
    }

    // lastNumberSegment

    @Test
    fun lastNumberSegment_noOperator_returnsWhole() {
        assertEquals("123", ExpressionEvaluator.lastNumberSegment("123"))
    }

    @Test
    fun lastNumberSegment_singleOperator_returnsAfter() {
        assertEquals("9", ExpressionEvaluator.lastNumberSegment("856x8+9"))
    }

    @Test
    fun lastNumberSegment_decimalNumber_returnsCorrect() {
        assertEquals("0.5", ExpressionEvaluator.lastNumberSegment("5+0.5"))
    }

    @Test
    fun lastNumberSegment_negativeSecondOperand_includesSign() {
        assertEquals("-2", ExpressionEvaluator.lastNumberSegment("356x-2"))
    }

    @Test
    fun lastNumberSegment_negativeAfterPlus_includesSign() {
        assertEquals("-3", ExpressionEvaluator.lastNumberSegment("5+-3"))
    }

    @Test
    fun lastNumberSegment_leadingNegative_returnsWhole() {
        assertEquals("-5", ExpressionEvaluator.lastNumberSegment("-5"))
    }

    @Test
    fun lastNumberSegment_minusAsOperator_returnsAfter() {
        assertEquals("3", ExpressionEvaluator.lastNumberSegment("5-3"))
    }

    // error type distinction

    @Test
    fun evaluate_divisionByZero_returnsUndefined() {
        val result = ExpressionEvaluator.evaluate("5÷0", true)
        assertTrue(result is EvaluationResult.Error)
        assertEquals(CalculatorError.UNDEFINED, (result as EvaluationResult.Error).error)
    }

    @Test
    fun evaluate_sqrtNegative_returnsUndefined() {
        val result = ExpressionEvaluator.evaluate("sqrt(-1)", true)
        assertTrue(result is EvaluationResult.Error)
        assertEquals(CalculatorError.UNDEFINED, (result as EvaluationResult.Error).error)
    }

    @Test
    fun evaluate_emptyFunction_returnsMathError() {
        val result = ExpressionEvaluator.evaluate("sin()", true)
        assertTrue(result is EvaluationResult.Error)
        assertEquals(CalculatorError.MATH_ERROR, (result as EvaluationResult.Error).error)
    }

    @Test
    fun evaluate_zeroToZero_returnsUndefined() {
        val result = ExpressionEvaluator.evaluate("0^0", true)
        assertTrue(result is EvaluationResult.Error)
        assertEquals(CalculatorError.UNDEFINED, (result as EvaluationResult.Error).error)
    }

    // autoBalanceParens — via evaluate with isFinal = true

    @Test
    fun evaluate_unclosedFunction_preview_returnsNoOp() {
        val result = ExpressionEvaluator.evaluate("sin(30", false, false) // DEG
        assertEquals(EvaluationResult.NoOp, result)
    }

    @Test
    fun evaluate_unclosedFunction_isFinal_autoBalancesAndEvaluates() {
        val result = ExpressionEvaluator.evaluate("sin(30", true, false) // DEG
        assertTrue(result is EvaluationResult.Success)
        assertEquals(0.5, (result as EvaluationResult.Success).value, 1e-9)
    }

    @Test
    fun evaluate_nestedUnclosed_isFinal_autoBalancesAll() {
        val result = ExpressionEvaluator.evaluate("sin(cos(0", true, true) // RAD
        assertTrue(result is EvaluationResult.Success)
        assertEquals(kotlin.math.sin(1.0), (result as EvaluationResult.Success).value, 1e-9)
    }

    @Test
    fun evaluate_alreadyBalanced_isFinal_unchanged() {
        val result = ExpressionEvaluator.evaluate("sin(30)", true, false) // DEG
        assertTrue(result is EvaluationResult.Success)
        assertEquals(0.5, (result as EvaluationResult.Success).value, 1e-9)
    }
}
