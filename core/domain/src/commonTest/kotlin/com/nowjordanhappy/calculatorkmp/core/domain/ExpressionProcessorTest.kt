package com.nowjordanhappy.calculatorkmp.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
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

    // formatDisplay

    @Test
    fun formatDisplay_largeNumber_usesScientificNotation() {
        assertEquals("1E10", processor.formatDisplay(1e10))
    }

    @Test
    fun formatDisplay_belowThreshold_usesPlain() {
        assertEquals("9999999999", processor.formatDisplay(9_999_999_999.0))
    }

    @Test
    fun formatDisplay_smallNumber_usesScientificNotation() {
        assertEquals("1E-7", processor.formatDisplay(1e-7))
    }

    @Test
    fun formatDisplay_aboveSmallThreshold_usesPlain() {
        assertEquals("0.000001", processor.formatDisplay(1e-6))
    }

    @Test
    fun formatDisplay_negative_largeNumber_usesScientificNotation() {
        assertEquals("-1E10", processor.formatDisplay(-1e10))
    }

    @Test
    fun formatDisplay_zero_usesPlain() {
        assertEquals("0", processor.formatDisplay(0.0))
    }

    @Test
    fun formatDisplay_decimal_usesPlain() {
        assertEquals("1.5", processor.formatDisplay(1.5))
    }

    @Test
    fun formatDisplay_mantissaTrimsTrailingZeros() {
        assertEquals("1.5E10", processor.formatDisplay(1.5e10))
    }

    // formatResult

    @Test
    fun formatResult_integer_noDecimalPoint() {
        assertEquals("10", processor.formatResult(10.0))
    }

    @Test
    fun formatResult_decimal_stripsTrailingZeros() {
        assertEquals("3.5", processor.formatResult(3.50))
    }

    @Test
    fun formatResult_smallDecimal_noScientificNotation() {
        assertEquals("0.000005", processor.formatResult(0.000005))
    }

    @Test
    fun formatResult_largeWhole_noDecimalPoint() {
        assertEquals("1000000000", processor.formatResult(1_000_000_000.0))
    }

    // applyPercent

    @Test
    fun applyPercent_singleNumber_dividesBy100() {
        assertEquals("0.5", processor.applyPercent("50", true))
    }

    @Test
    fun applyPercent_afterAdd_percentOfBase() {
        assertEquals("100+10", processor.applyPercent("100+10", true))
    }

    @Test
    fun applyPercent_afterMultiply_dividesBy100() {
        assertEquals("5x0.1", processor.applyPercent("5x10", true))
    }

    @Test
    fun applyPercent_nonNumericLastSegment_returnsNull() {
        assertNull(processor.applyPercent("5+", true))
    }

    // applySignToggle

    @Test
    fun applySignToggle_positive_becomesNegative() {
        assertEquals("-5", processor.applySignToggle("5"))
    }

    @Test
    fun applySignToggle_negative_becomesPositive() {
        assertEquals("5", processor.applySignToggle("-5"))
    }

    @Test
    fun applySignToggle_afterOperator_togglesSecondOperand() {
        assertEquals("5+-3", processor.applySignToggle("5+3"))
    }

    @Test
    fun applySignToggle_afterOperatorNegative_removesSign() {
        assertEquals("5+3", processor.applySignToggle("5+-3"))
    }

    @Test
    fun applySignToggle_emptyExpression_returnsNull() {
        assertNull(processor.applySignToggle(""))
    }
}
