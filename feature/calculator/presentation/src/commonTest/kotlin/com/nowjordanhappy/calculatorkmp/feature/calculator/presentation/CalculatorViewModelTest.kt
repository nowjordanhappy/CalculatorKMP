package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation

import com.nowjordanhappy.calculatorkmp.core.domain.CalculatorUtils
import com.nowjordanhappy.calculatorkmp.core.domain.Constants
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
    fun onResolveClick_trailingOperator_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("5+", viewModel.state.value.expression)
        assertEquals(null, viewModel.state.value.error)
    }

    // Point

    @Test
    fun onPointClick_addsPoint() {
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnPointClick)
        assertEquals("3.", viewModel.state.value.expression)
    }

    @Test
    fun onPointClick_emptyExpression_produces0Point() {
        viewModel.onAction(CalculatorAction.OnPointClick)
        assertEquals("0.", viewModel.state.value.expression)
    }

    @Test
    fun onPointClick_emptyThenDigit_produces0PointDigit() {
        viewModel.onAction(CalculatorAction.OnPointClick)
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        assertEquals("0.2", viewModel.state.value.expression)
    }

    @Test
    fun onPointClick_trailingDot_thenOperator_stripsAndAddsOperator() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnPointClick)
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        assertEquals("5x", viewModel.state.value.expression)
    }

    @Test
    fun onPointClick_trailingDot_thenMultiply_resolvesCorrectly() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnPointClick)
        viewModel.onAction(CalculatorAction.OnOperatorClick("x"))
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("25", viewModel.state.value.expression)
    }

    @Test
    fun onPointClick_trailingDot_thenResolve_stripsPoint() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnPointClick)
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("5", viewModel.state.value.expression)
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
    fun onNumberClick_whileErrorShown_startsFresh() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
        viewModel.onAction(CalculatorAction.OnNumberClick("9"))
        assertEquals("9", viewModel.state.value.expression)
        assertEquals(null, viewModel.state.value.error)
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

    // Smart delete — function prefix as unit

    @Test
    fun onDeleteClick_functionPrefix_deletesWholePrefix() {
        viewModel.onAction(CalculatorAction.OnScientificFunction("sin"))
        assertEquals("sin(", viewModel.state.value.expression)
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertEquals("", viewModel.state.value.expression)
    }

    @Test
    fun onDeleteClick_longFunctionPrefix_deletesWholePrefix() {
        viewModel.onAction(CalculatorAction.OnScientificFunction("sin⁻¹"))
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertEquals("", viewModel.state.value.expression)
    }

    @Test
    fun onDeleteClick_functionWithArg_deletesArgCharByChar() {
        viewModel.onAction(CalculatorAction.OnScientificFunction("sin"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertEquals("sin(", viewModel.state.value.expression)
    }

    @Test
    fun onDeleteClick_nestedFunction_deletesInnerPrefix() {
        viewModel.onAction(CalculatorAction.OnScientificFunction("sin"))
        viewModel.onAction(CalculatorAction.OnScientificFunction("cos"))
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertEquals("sin(", viewModel.state.value.expression)
    }

    @Test
    fun onDeleteClick_standaloneParen_deletesJustParen() {
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnScientificFunction("("))
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertEquals("3+", viewModel.state.value.expression)
    }

    // Close paren block

    @Test
    fun closeParen_noOpenParen_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(")"))
        assertEquals("5", viewModel.state.value.expression)
    }

    @Test
    fun closeParen_withOpenParen_appends() {
        viewModel.onAction(CalculatorAction.OnScientificFunction("sin"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(")"))
        assertEquals("sin(3)", viewModel.state.value.expression)
    }

    @Test
    fun closeParen_allParensClosed_doesNothing() {
        viewModel.onAction(CalculatorAction.OnScientificFunction("sin"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(")"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(")"))
        assertEquals("sin(3)", viewModel.state.value.expression)
    }

    // AC/C toggle

    @Test
    fun isAcMode_initialState_isTrue() {
        assertTrue(viewModel.state.value.isAcMode)
    }

    @Test
    fun isAcMode_afterDigit_isFalse() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        assertFalse(viewModel.state.value.isAcMode)
    }

    @Test
    fun isAcMode_afterResolve_isTrue() {
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.isAcMode)
    }

    @Test
    fun isAcMode_afterClear_isTrue() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnClearClick)
        assertTrue(viewModel.state.value.isAcMode)
    }

    @Test
    fun isAcMode_afterError_isTrue() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.isAcMode)
    }

    @Test
    fun isAcMode_afterDeleteToEmpty_isTrue() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertTrue(viewModel.state.value.isAcMode)
    }

    @Test
    fun isAcMode_afterDeleteWithRemainingExpression_isFalse() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertFalse(viewModel.state.value.isAcMode)
    }

    // Error state interaction — percent, sign toggle, scientific functions

    @Test
    fun onPercentClick_whileErrorShown_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
        viewModel.onAction(CalculatorAction.OnPercentClick)
        assertTrue(viewModel.state.value.error != null)
    }

    @Test
    fun onSignToggleClick_whileErrorShown_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
        viewModel.onAction(CalculatorAction.OnSignToggleClick)
        assertTrue(viewModel.state.value.error != null)
    }

    @Test
    fun onScientificFunction_whileErrorShown_startsFresh() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
        viewModel.onAction(CalculatorAction.OnScientificFunction("sin"))
        assertEquals("sin(", viewModel.state.value.expression)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun onScientificFunction_constant_whileErrorShown_startsFresh() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
        viewModel.onAction(CalculatorAction.OnScientificFunction("π"))
        assertEquals("π", viewModel.state.value.expression)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun onScientificFunction_closeParen_whileErrorShown_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
        viewModel.onAction(CalculatorAction.OnScientificFunction(")"))
        assertTrue(viewModel.state.value.error != null)
        assertEquals("5÷0", viewModel.state.value.expression)
    }

    @Test
    fun onPointClick_whileErrorShown_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
        viewModel.onAction(CalculatorAction.OnPointClick)
        assertTrue(viewModel.state.value.error != null)
    }

    @Test
    fun onDeleteClick_whileErrorShown_doesNothing() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertTrue(viewModel.state.value.error != null)
        viewModel.onAction(CalculatorAction.OnDeleteClick)
        assertTrue(viewModel.state.value.error != null)
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

    // Implicit multiplication

    @Test
    fun digitAfterCloseParen_insertsMultiply() {
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.SIN))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.CLOSE_PAREN))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        assertEquals("sin(30)x2", viewModel.state.value.expression)
    }

    @Test
    fun digitAfterPi_insertsMultiply() {
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.PI))
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        assertEquals("πx2", viewModel.state.value.expression)
    }

    @Test
    fun digitAfterE_insertsMultiply() {
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.E))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        assertEquals("ex3", viewModel.state.value.expression)
    }

    @Test
    fun functionAfterDigit_insertsMultiply() {
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.SIN))
        assertEquals("2xsin(", viewModel.state.value.expression)
    }

    @Test
    fun functionAfterCloseParen_insertsMultiply() {
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.SIN))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnNumberClick("0"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.CLOSE_PAREN))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.COS))
        assertEquals("sin(30)xcos(", viewModel.state.value.expression)
    }

    @Test
    fun piAfterDigit_insertsMultiply() {
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.PI))
        assertEquals("2xπ", viewModel.state.value.expression)
    }

    @Test
    fun openParenAfterDigit_insertsMultiply() {
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.OPEN_PAREN))
        assertEquals("2x(", viewModel.state.value.expression)
    }

    @Test
    fun functionAfterOperator_noImplicitMultiply() {
        viewModel.onAction(CalculatorAction.OnNumberClick("5"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.SIN))
        assertEquals("5+sin(", viewModel.state.value.expression)
    }

    @Test
    fun functionFromEmpty_noImplicitMultiply() {
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.SIN))
        assertEquals("sin(", viewModel.state.value.expression)
    }

    @Test
    fun implicitMultiply_evaluatesCorrectly() {
        viewModel.onAction(CalculatorAction.OnNumberClick("2"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.OPEN_PAREN))
        viewModel.onAction(CalculatorAction.OnNumberClick("3"))
        viewModel.onAction(CalculatorAction.OnOperatorClick("+"))
        viewModel.onAction(CalculatorAction.OnNumberClick("4"))
        viewModel.onAction(CalculatorAction.OnScientificFunction(ButtonLabels.Scientific.CLOSE_PAREN))
        viewModel.onAction(CalculatorAction.OnResolveClick)
        assertEquals("14", viewModel.state.value.expression)
    }
}
