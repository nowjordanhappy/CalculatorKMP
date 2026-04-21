package com.nowjordanhappy.calculatorkmp.core.domain.fsm

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CalculatorFSMTest {

    private lateinit var fsm: CalculatorFSM

    @BeforeTest
    fun setup() {
        fsm = CalculatorFSM(BasicStrategy())
    }

    // Empty state

    @Test
    fun empty_digit_movesToFirstOperand() {
        fsm.process(FSMAction.Digit("5"))
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun empty_minus_movesToFirstOperand() {
        fsm.process(FSMAction.Operator("-"))
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun empty_nonMinusOperator_blocks() {
        val result = fsm.process(FSMAction.Operator("+"))
        assertIs<FSMTransition.Block>(result)
        assertEquals(FSMState.Empty, fsm.state)
    }

    @Test
    fun empty_point_blocks() {
        val result = fsm.process(FSMAction.Point)
        assertIs<FSMTransition.Block>(result)
    }

    @Test
    fun empty_clear_staysEmpty() {
        fsm.process(FSMAction.Clear)
        assertEquals(FSMState.Empty, fsm.state)
    }

    // FirstOperand state

    @Test
    fun firstOperand_operator_movesToOperatorEntered() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        assertEquals(FSMState.OperatorEntered, fsm.state)
    }

    @Test
    fun firstOperand_resolve_movesToResult() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Result, fsm.state)
    }

    @Test
    fun firstOperand_clear_movesToEmpty() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Clear)
        assertEquals(FSMState.Empty, fsm.state)
    }

    // OperatorEntered state

    @Test
    fun operatorEntered_digit_movesToSecondOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Digit("3"))
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun operatorEntered_minus_movesToSecondOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Operator("-"))
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun operatorEntered_nonMinusOperator_replacesOperator() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Operator("x"))
        assertEquals(FSMState.OperatorEntered, fsm.state)
    }

    @Test
    fun operatorEntered_delete_movesToFirstOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Delete)
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun operatorEntered_resolve_movesToError() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Error, fsm.state)
    }

    // SecondOperand state

    @Test
    fun secondOperand_resolve_movesToResult() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Digit("3"))
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Result, fsm.state)
    }

    @Test
    fun secondOperand_operator_movesToOperatorEntered() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Digit("3"))
        fsm.process(FSMAction.Operator("x"))
        assertEquals(FSMState.OperatorEntered, fsm.state)
    }

    // Result state

    @Test
    fun result_digit_movesToFirstOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Resolve)
        fsm.process(FSMAction.Digit("9"))
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun result_operator_movesToOperatorEntered() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Resolve)
        fsm.process(FSMAction.Operator("+"))
        assertEquals(FSMState.OperatorEntered, fsm.state)
    }

    @Test
    fun result_resolve_staysResult() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Resolve)
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Result, fsm.state)
    }

    @Test
    fun result_delete_movesToFirstOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Resolve)
        fsm.process(FSMAction.Delete)
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun result_point_blocks() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Resolve)
        val result = fsm.process(FSMAction.Point)
        assertIs<FSMTransition.Block>(result)
    }

    // syncFromExpression

    @Test
    fun syncFromExpression_empty_setsEmpty() {
        fsm.syncFromExpression("")
        assertEquals(FSMState.Empty, fsm.state)
    }

    @Test
    fun syncFromExpression_singleNumber_setsFirstOperand() {
        fsm.syncFromExpression("5")
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun syncFromExpression_negativeNumber_setsFirstOperand() {
        fsm.syncFromExpression("-5")
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun syncFromExpression_endsWithOperator_setsOperatorEntered() {
        fsm.syncFromExpression("5+")
        assertEquals(FSMState.OperatorEntered, fsm.state)
    }

    @Test
    fun syncFromExpression_endsWithNegativeSign_setsSecondOperand() {
        fsm.syncFromExpression("5+-")
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun syncFromExpression_twoOperands_setsSecondOperand() {
        fsm.syncFromExpression("5+3")
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    // Error state

    @Test
    fun error_clear_movesToEmpty() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Error, fsm.state)
        fsm.process(FSMAction.Clear)
        assertEquals(FSMState.Empty, fsm.state)
    }

    @Test
    fun error_digit_blocks() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Resolve)
        val result = fsm.process(FSMAction.Digit("3"))
        assertIs<FSMTransition.Block>(result)
    }
}
