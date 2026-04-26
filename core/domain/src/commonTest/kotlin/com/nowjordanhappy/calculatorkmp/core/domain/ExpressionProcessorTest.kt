package com.nowjordanhappy.calculatorkmp.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ExpressionProcessorTest {
    private val processor = ExpressionProcessor()

    // evaluate

    @Test
    fun evaluate_validExpression_returnsSuccess() {
        val result = processor.evaluate("-5x2.5", true)
        assertIs<EvaluationResult.Success>(result)
    }

    @Test
    fun evaluate_emptyExpression_returnsNoOp() {
        val result = processor.evaluate("", true)
        assertIs<EvaluationResult.NoOp>(result)
    }

    // addPoint

    @Test
    fun addPoint_firstPoint_returnsTrue() {
        assertTrue(processor.addPoint("3x2"))
    }

    @Test
    fun addPoint_secondPointSameNumber_returnsFalse() {
        assertFalse(processor.addPoint("3.5x2.1"))
    }

    @Test
    fun addPoint_secondPointNewNumber_returnsTrue() {
        assertTrue(processor.addPoint("3.5x2"))
    }

    @Test
    fun addPoint_trailingPoint_returnsFalse() {
        assertFalse(processor.addPoint("3.5x2."))
    }

    @Test
    fun addPoint_multiOperator_lastSegmentNoPoint_returnsTrue() {
        assertTrue(processor.addPoint("5.1+3"))
    }

    @Test
    fun addPoint_multiOperator_lastSegmentHasPoint_returnsFalse() {
        assertFalse(processor.addPoint("5+3.1"))
    }

    // appendDigit

    @Test
    fun appendDigit_toEmpty_returnsDigit() {
        assertEquals("5", processor.appendDigit("", "5"))
    }

    @Test
    fun appendDigit_nonZeroAfterLeadingZero_replacesZero() {
        assertEquals("5", processor.appendDigit("0", "5"))
    }

    @Test
    fun appendDigit_zeroAfterLeadingZero_staysZero() {
        assertEquals("0", processor.appendDigit("0", "0"))
    }

    @Test
    fun appendDigit_zeroAfterOperator_appends() {
        assertEquals("5+0", processor.appendDigit("5+", "0"))
    }

    @Test
    fun appendDigit_doubleZeroAfterOperator_blocked() {
        assertEquals("5+0", processor.appendDigit("5+0", "0"))
    }

    @Test
    fun appendDigit_nonZeroReplacesLeadingZeroAfterOperator() {
        assertEquals("5+3", processor.appendDigit("5+0", "3"))
    }

    @Test
    fun appendDigit_afterCloseParen_insertsMultiply() {
        assertEquals("sin(30)x2", processor.appendDigit("sin(30)", "2"))
    }

    @Test
    fun appendDigit_afterPi_insertsMultiply() {
        assertEquals("πx2", processor.appendDigit("π", "2"))
    }

    @Test
    fun appendDigit_afterDigit_appends() {
        assertEquals("53", processor.appendDigit("5", "3"))
    }

    // needsImplicitMultiply

    @Test
    fun needsImplicitMultiply_empty_returnsFalse() {
        assertFalse(processor.needsImplicitMultiply(""))
    }

    @Test
    fun needsImplicitMultiply_endsWithDigit_returnsTrue() {
        assertTrue(processor.needsImplicitMultiply("5"))
    }

    @Test
    fun needsImplicitMultiply_endsWithCloseParen_returnsTrue() {
        assertTrue(processor.needsImplicitMultiply("sin(30)"))
    }

    @Test
    fun needsImplicitMultiply_endsWithPi_returnsTrue() {
        assertTrue(processor.needsImplicitMultiply("π"))
    }

    @Test
    fun needsImplicitMultiply_endsWithOperator_returnsFalse() {
        assertFalse(processor.needsImplicitMultiply("5+"))
    }

    @Test
    fun needsImplicitMultiply_endsWithOpenParen_returnsFalse() {
        assertFalse(processor.needsImplicitMultiply("sin("))
    }
}
