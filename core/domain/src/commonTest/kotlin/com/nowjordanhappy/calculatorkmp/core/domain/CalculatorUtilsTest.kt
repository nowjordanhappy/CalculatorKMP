package com.nowjordanhappy.calculatorkmp.core.domain

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CalculatorUtilsTest {
    private val utils = CalculatorUtils()

    // checkOrResolve

    @Test
    fun checkOrResolve_validExpression_returnsSuccess() {
        val result = utils.checkOrResolve("-5x2.5", true)
        assertIs<OperationResult.Success>(result)
    }

    @Test
    fun checkOrResolve_emptyExpression_returnsNoOp() {
        val result = utils.checkOrResolve("", true)
        assertIs<OperationResult.NoOp>(result)
    }

    // addPoint

    @Test
    fun addPoint_firstPoint_returnsTrue() {
        assertTrue(utils.addPoint("3x2"))
    }

    @Test
    fun addPoint_secondPointSameNumber_returnsFalse() {
        assertFalse(utils.addPoint("3.5x2.1"))
    }

    @Test
    fun addPoint_secondPointNewNumber_returnsTrue() {
        assertTrue(utils.addPoint("3.5x2"))
    }

    @Test
    fun addPoint_trailingPoint_returnsFalse() {
        assertFalse(utils.addPoint("3.5x2."))
    }

    @Test
    fun addPoint_multiOperator_lastSegmentNoPoint_returnsTrue() {
        assertTrue(utils.addPoint("5.1+3"))
    }

    @Test
    fun addPoint_multiOperator_lastSegmentHasPoint_returnsFalse() {
        assertFalse(utils.addPoint("5+3.1"))
    }
}
