package com.nowjordanhappy.calculatorkmp.core.domain

import kotlin.test.Test
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
}
