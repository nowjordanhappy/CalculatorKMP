package com.jordanrojas.calculatorkmp.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OperationsTest {

    // canReplaceOperator

    @Test
    fun canReplaceOperator_doubleOperator_returnsTrue() {
        assertTrue(Operations.canReplaceOperator("5+x"))
    }

    @Test
    fun canReplaceOperator_singleOperator_returnsFalse() {
        assertFalse(Operations.canReplaceOperator("5+"))
    }

    @Test
    fun canReplaceOperator_shortExpression_returnsFalse() {
        assertFalse(Operations.canReplaceOperator("+"))
    }

    // tryResolve — single operator

    @Test
    fun tryResolve_multiplication_returnsSuccess() {
        val result = Operations.tryResolve("5x2", true)
        assertTrue(result is OperationResult.Success)
        assertEquals(10.0, (result as OperationResult.Success).value)
    }

    @Test
    fun tryResolve_division_returnsSuccess() {
        val result = Operations.tryResolve("10÷2", true)
        assertTrue(result is OperationResult.Success)
        assertEquals(5.0, (result as OperationResult.Success).value)
    }

    @Test
    fun tryResolve_sum_returnsSuccess() {
        val result = Operations.tryResolve("3+4", true)
        assertTrue(result is OperationResult.Success)
        assertEquals(7.0, (result as OperationResult.Success).value)
    }

    @Test
    fun tryResolve_subtraction_returnsSuccess() {
        val result = Operations.tryResolve("10-3", true)
        assertTrue(result is OperationResult.Success)
        assertEquals(7.0, (result as OperationResult.Success).value)
    }

    // tryResolve — multiple operators with precedence

    @Test
    fun tryResolve_multipleOperators_respectsPrecedence() {
        val result = Operations.tryResolve("856x8+9", true)
        assertTrue(result is OperationResult.Success)
        assertEquals(6857.0, (result as OperationResult.Success).value)
    }

    @Test
    fun tryResolve_addThenMultiply_respectsPrecedence() {
        val result = Operations.tryResolve("2+3x4", true)
        assertTrue(result is OperationResult.Success)
        assertEquals(14.0, (result as OperationResult.Success).value)
    }

    @Test
    fun tryResolve_multipleAdditions_leftToRight() {
        val result = Operations.tryResolve("1+2+3", true)
        assertTrue(result is OperationResult.Success)
        assertEquals(6.0, (result as OperationResult.Success).value)
    }

    @Test
    fun tryResolve_negativeSecondOperand_works() {
        val result = Operations.tryResolve("5+-3", true)
        assertTrue(result is OperationResult.Success)
        assertEquals(2.0, (result as OperationResult.Success).value)
    }

    // tryResolve — edge cases

    @Test
    fun tryResolve_emptyExpression_returnsNoOp() {
        assertEquals(OperationResult.NoOp, Operations.tryResolve("", true))
    }

    @Test
    fun tryResolve_incompleteExpression_isFromResolve_returnsError() {
        val result = Operations.tryResolve("5+", true)
        assertTrue(result is OperationResult.Error)
        assertEquals(CalculatorError.INCORRECT_EXPRESSION, (result as OperationResult.Error).error)
    }

    @Test
    fun tryResolve_trailingPoint_stripsPointAndReturnsNoOp() {
        assertEquals(OperationResult.NoOp, Operations.tryResolve("5.", true))
    }

    @Test
    fun tryResolve_divisionByZero_returnsError() {
        val result = Operations.tryResolve("5÷0", true)
        assertTrue(result is OperationResult.Error)
    }

    // lastNumberSegment

    @Test
    fun lastNumberSegment_noOperator_returnsWhole() {
        assertEquals("123", Operations.lastNumberSegment("123"))
    }

    @Test
    fun lastNumberSegment_singleOperator_returnsAfter() {
        assertEquals("9", Operations.lastNumberSegment("856x8+9"))
    }

    @Test
    fun lastNumberSegment_decimalNumber_returnsCorrect() {
        assertEquals("0.5", Operations.lastNumberSegment("5+0.5"))
    }

    @Test
    fun lastNumberSegment_negativeSecondOperand_includesSign() {
        assertEquals("-2", Operations.lastNumberSegment("356x-2"))
    }

    @Test
    fun lastNumberSegment_negativeAfterPlus_includesSign() {
        assertEquals("-3", Operations.lastNumberSegment("5+-3"))
    }

    @Test
    fun lastNumberSegment_leadingNegative_returnsWhole() {
        assertEquals("-5", Operations.lastNumberSegment("-5"))
    }

    @Test
    fun lastNumberSegment_minusAsOperator_returnsAfter() {
        assertEquals("3", Operations.lastNumberSegment("5-3"))
    }
}
