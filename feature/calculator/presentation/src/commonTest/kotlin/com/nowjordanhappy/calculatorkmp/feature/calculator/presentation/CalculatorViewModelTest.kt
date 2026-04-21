package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation

import com.nowjordanhappy.calculatorkmp.core.domain.CalculatorUtils
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    @BeforeTest
    fun setup() {
        viewModel = CalculatorViewModel(CalculatorUtils())
    }

    // Number input

    @Test
    fun onNumberClick_appendsToExpression() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        assertEquals("5", viewModel.state.value.expression)
    }

    @Test
    fun onNumberClick_multipleDigits_appendsAll() {
        viewModel.onAction(CalculatorAction.OnNumberClick("1"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        assertEquals("123", viewModel.state.value.expression)
    }

    // Leading zeros

    @Test
    fun onNumberClick_digitAfterLeadingZero_replacesZero() {
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        assertEquals("5", viewModel.state.value.expression)
    }

    @Test
    fun onNumberClick_doubleZero_staysZero() {
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        assertEquals("0", viewModel.state.value.expression)
    }

    @Test
    fun onNumberClick_afterOperatorZeroThenDigit_replacesZero() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        assertEquals("5+3", viewModel.state.value.expression)
    }

    @Test
    fun onNumberClick_afterOperatorDoubleZero_staysZero() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        assertEquals("5+0", viewModel.state.value.expression)
    }

    // Operators

    @Test
    fun onOperatorClick_appendsOperator() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        assertEquals("5+", viewModel.state.value.expression)
    }

    @Test
    fun onOperatorClick_replacesDoubleOperator() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        assertEquals("5x", viewModel.state.value.expression)
    }

    // Clear and delete

    @Test
    fun onClearClick_resetsState() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnClearClick)
        assertEquals("", viewModel.state.value.expression)
        assertEquals("", viewModel.state.value.result)
    }

    @Test
    fun onDeleteClick_removesLastChar() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertEquals("5", viewModel.state.value.expression)
    }

    @Test
    fun onDeleteClick_emptyExpression_doesNothing() {
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertEquals("", viewModel.state.value.expression)
    }

    // Resolve

    @Test
    fun onResolveClick_validExpression_setsResult() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("10", viewModel.state.value.expression)
    }

    @Test
    fun onResolveClick_incompleteExpression_setsError() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
    }

    // Point

    @Test
    fun onPointClick_addsPoint() {
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnPointClick)
        assertEquals("3.", viewModel.state.value.expression)
    }

    @Test
    fun onPointClick_doublePoint_ignored() {
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnPointClick)
        viewModel.onAction(CalculatorAction.OnPointClick)
        assertEquals("3.", viewModel.state.value.expression)
    }

    // Percent

    @Test
    fun onPercentClick_singleNumber_dividesBy100() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnPercentClick)
        assertEquals("0.5", viewModel.state.value.expression)
    }

    @Test
    fun onPercentClick_addContext_percentOfBase() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnPercentClick)
        assertEquals("5+1", viewModel.state.value.expression)
    }

    @Test
    fun onPercentClick_subtractContext_percentOfBase() {
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("-"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnPercentClick)
        assertEquals("200-50", viewModel.state.value.expression)
    }

    @Test
    fun onPercentClick_multiplyContext_dividesByHundred() {
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnPercentClick)
        assertEquals("200x0.5", viewModel.state.value.expression)
    }

    @Test
    fun onPercentClick_divideContext_dividesByHundred() {
        viewModel.onAction(CalculatorAction.OnNumberClick("1"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("÷"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnPercentClick)
        assertEquals("100÷0.25", viewModel.state.value.expression)
    }

    @Test
    fun onPercentClick_emptyExpression_doesNothing() {
        viewModel.onAction(CalculatorAction.OnPercentClick)
        assertEquals("", viewModel.state.value.expression)
    }

    // Sign toggle

    @Test
    fun onSignToggleClick_positiveNumber_negates() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertEquals("-5", viewModel.state.value.expression)
    }

    @Test
    fun onSignToggleClick_negativeNumber_removesSign() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertEquals("5", viewModel.state.value.expression)
    }

    @Test
    fun onSignToggleClick_secondOperand_negatesSecondAndUpdatesPreview() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertEquals("5+-3", viewModel.state.value.expression)
        assertEquals("2", viewModel.state.value.result)
    }

    @Test
    fun onSignToggleClick_emptyExpression_doesNothing() {
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertEquals("", viewModel.state.value.expression)
    }

    @Test
    fun onSignToggleClick_multiOperator_negatesLastOperand() {
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnNumberClick("6"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertEquals("356x-2", viewModel.state.value.expression)
    }

    @Test
    fun onSignToggleClick_multiOperator_doubleToggleRestores() {
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnNumberClick("6"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertEquals("356x2", viewModel.state.value.expression)
    }

    @Test
    fun onSignToggleClick_removesNegativeSecondOperand() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertEquals("5+3", viewModel.state.value.expression)
    }

    // Multi-operator resolve

    @Test
    fun onResolveClick_multiOperator_respectsPrecedence() {
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        viewModel.onAction(CalculatorAction.OnNumberClick("4"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("14", viewModel.state.value.expression)
    }

    @Test
    fun onResolveClick_multiOperator_multiplyThenAdd() {
        viewModel.onAction(CalculatorAction.OnNumberClick("8"))
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnNumberClick("6"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        viewModel.onAction(CalculatorAction.OnNumberClick("8"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("9"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("6857", viewModel.state.value.expression)
    }

    @Test
    fun onResolveClick_divisionThenAddition() {
        viewModel.onAction(CalculatorAction.OnNumberClick("1"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("÷"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("8", viewModel.state.value.expression)
    }

    // Chained =

    @Test
    fun onResolveClick_thenOperator_continuesFromResult() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("10", viewModel.state.value.expression)
    }

    @Test
    fun onResolveClick_thenNumber_startsFresh() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        viewModel.onAction(CalculatorAction.OnNumberClick("9"))
        assertEquals("9", viewModel.state.value.expression)
    }

    // Delete in Result state

    @Test
    fun onDeleteClick_inResultState_editDigitByDigit() {
        viewModel.onAction(CalculatorAction.OnNumberClick("8"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("10", viewModel.state.value.expression)
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertEquals("1", viewModel.state.value.expression)
    }

    @Test
    fun onDeleteClick_inResultState_toEmpty_allowsNewInput() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        viewModel.onAction(CalculatorAction.OnNumberClick("9"))
        assertEquals("9", viewModel.state.value.expression)
    }

    // Percent in Result state

    @Test
    fun onPercentClick_inResultState_appliesPercent() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        viewModel.onAction(CalculatorAction.OnPercentClick)
        assertEquals("0.5", viewModel.state.value.expression)
    }

    // Sign toggle in Result state

    @Test
    fun onSignToggleClick_inResultState_negatesResult() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertEquals("-5", viewModel.state.value.expression)
    }

    // Division by zero

    @Test
    fun onResolveClick_divisionByZero_setsError() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("÷"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
    }

    // Error state blocked by FSM

    @Test
    fun onNumberClick_whileErrorShown_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        val exprBeforeNumber = viewModel.state.value.expression
        viewModel.onAction(CalculatorAction.OnNumberClick("9"))
        assertEquals(exprBeforeNumber, viewModel.state.value.expression)
    }

    @Test
    fun onOperatorClick_whileErrorShown_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        val exprBefore = viewModel.state.value.expression
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        assertEquals(exprBefore, viewModel.state.value.expression)
    }

    // Error dismiss

    @Test
    fun onErrorDismiss_clearsError() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        viewModel.onAction(CalculatorAction.OnErrorDismiss)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun onErrorDismiss_allowsContinuedTyping() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        viewModel.onAction(CalculatorAction.OnErrorDismiss)
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        assertEquals("5+3", viewModel.state.value.expression)
    }

    // formatResult — no scientific notation

    @Test
    fun formatResult_smallDecimal_noScientificNotation() {
        val result = viewModel.formatResult(0.000005)
        assertEquals("0.000005", result)
    }

    @Test
    fun formatResult_integer_noDecimalPoint() {
        val result = viewModel.formatResult(10.0)
        assertEquals("10", result)
    }

    @Test
    fun formatResult_decimal_stripsTrailingZeros() {
        val result = viewModel.formatResult(3.50)
        assertEquals("3.5", result)
    }
}
