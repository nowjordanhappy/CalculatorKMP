package com.nowjordanhappy.calculatorkmp.core.domain.fsm

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ScientificStrategyTest {
    private lateinit var fsm: CalculatorFSM

    @BeforeTest
    fun setup() {
        fsm = CalculatorFSM(ScientificStrategy())
    }

    // Basic ops still work (delegation to BasicStrategy)

    @Test
    fun basic_digit_movesToFirstOperand() {
        fsm.process(FSMAction.Digit("5"))
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun basic_operatorFromFirstOperand_movesToOperatorEntered() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        assertEquals(FSMState.OperatorEntered, fsm.state)
    }

    @Test
    fun basic_resolveFromSecondOperand_movesToResult() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Digit("3"))
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Result, fsm.state)
    }

    // Function

    @Test
    fun function_fromEmpty_movesToSecondOperand() {
        fsm.process(FSMAction.Function)
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun function_fromOperatorEntered_movesToSecondOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Function)
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun function_fromSecondOperand_movesToSecondOperand() {
        // nested: sin(cos(30))
        fsm.process(FSMAction.Function) // sin(
        fsm.process(FSMAction.Function) // cos( — nested
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun function_fromResult_movesToSecondOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Resolve)
        fsm.process(FSMAction.Function)
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun function_fromFirstOperand_blocks() {
        fsm.process(FSMAction.Digit("5"))
        val result = fsm.process(FSMAction.Function)
        assertIs<FSMTransition.Block>(result)
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    // OpenParen

    @Test
    fun openParen_fromEmpty_movesToSecondOperand() {
        fsm.process(FSMAction.OpenParen)
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun openParen_fromOperatorEntered_movesToSecondOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.OpenParen)
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun openParen_fromFirstOperand_blocks() {
        fsm.process(FSMAction.Digit("5"))
        val result = fsm.process(FSMAction.OpenParen)
        assertIs<FSMTransition.Block>(result)
    }

    // CloseParen

    @Test
    fun closeParen_fromFirstOperand_movesToSecondOperand() {
        fsm.process(FSMAction.OpenParen) // ( → SecondOperand
        fsm.process(FSMAction.Digit("5")) // 5 → SecondOperand
        fsm.process(FSMAction.Operator("-")) // - → OperatorEntered (unary, but FSM treats as operator)
        fsm.process(FSMAction.Digit("2")) // 2 → SecondOperand
        // For CloseParen from FirstOperand test, set state directly via sync
        fsm.syncFromExpression("5") // → FirstOperand
        fsm.process(FSMAction.CloseParen)
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun closeParen_fromSecondOperand_movesToSecondOperand() {
        fsm.process(FSMAction.Function) // sin( → SecondOperand
        fsm.process(FSMAction.Digit("3")) // 3 → SecondOperand
        fsm.process(FSMAction.Digit("0")) // 0 → SecondOperand
        fsm.process(FSMAction.CloseParen) // ) → SecondOperand
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun closeParen_fromEmpty_blocks() {
        val result = fsm.process(FSMAction.CloseParen)
        assertIs<FSMTransition.Block>(result)
    }

    @Test
    fun closeParen_fromOperatorEntered_blocks() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        val result = fsm.process(FSMAction.CloseParen)
        assertIs<FSMTransition.Block>(result)
    }

    // Constant

    @Test
    fun constant_fromEmpty_movesToFirstOperand() {
        fsm.process(FSMAction.Constant)
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun constant_fromOperatorEntered_movesToSecondOperand() {
        fsm.process(FSMAction.Digit("2"))
        fsm.process(FSMAction.Operator("x"))
        fsm.process(FSMAction.Constant)
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun constant_fromResult_movesToFirstOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Resolve)
        fsm.process(FSMAction.Constant)
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun constant_fromFirstOperand_blocks() {
        fsm.process(FSMAction.Digit("3"))
        val result = fsm.process(FSMAction.Constant)
        assertIs<FSMTransition.Block>(result)
    }

    // PowerSuffix (x², 1/x)

    @Test
    fun powerSuffix_fromFirstOperand_staysFirstOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.PowerSuffix)
        assertEquals(FSMState.FirstOperand, fsm.state)
    }

    @Test
    fun powerSuffix_fromSecondOperand_staysSecondOperand() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        fsm.process(FSMAction.Digit("3"))
        fsm.process(FSMAction.PowerSuffix)
        assertEquals(FSMState.SecondOperand, fsm.state)
    }

    @Test
    fun powerSuffix_fromEmpty_blocks() {
        val result = fsm.process(FSMAction.PowerSuffix)
        assertIs<FSMTransition.Block>(result)
    }

    @Test
    fun powerSuffix_fromOperatorEntered_blocks() {
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.Operator("+"))
        val result = fsm.process(FSMAction.PowerSuffix)
        assertIs<FSMTransition.Block>(result)
    }

    // Full sequences

    @Test
    fun sequence_sinExpression() {
        // sin(30) + 1
        fsm.process(FSMAction.Function) // sin(  → SecondOperand
        fsm.process(FSMAction.Digit("3")) //        → SecondOperand
        fsm.process(FSMAction.Digit("0")) //        → SecondOperand
        fsm.process(FSMAction.CloseParen) // )      → SecondOperand
        fsm.process(FSMAction.Operator("+")) // +      → OperatorEntered
        fsm.process(FSMAction.Digit("1")) //        → SecondOperand
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Result, fsm.state)
    }

    @Test
    fun sequence_nestedFunctions() {
        // sin(cos(0))
        fsm.process(FSMAction.Function) // sin(  → SecondOperand
        fsm.process(FSMAction.Function) // cos(  → SecondOperand
        fsm.process(FSMAction.Digit("0")) //        → SecondOperand
        fsm.process(FSMAction.CloseParen) // )      → SecondOperand
        fsm.process(FSMAction.CloseParen) // )      → SecondOperand
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Result, fsm.state)
    }

    @Test
    fun sequence_constantInExpression() {
        // 2 × π
        fsm.process(FSMAction.Digit("2"))
        fsm.process(FSMAction.Operator("x"))
        fsm.process(FSMAction.Constant) // π → SecondOperand
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Result, fsm.state)
    }

    @Test
    fun sequence_powerSuffix() {
        // 5 x²  → 5^2
        fsm.process(FSMAction.Digit("5"))
        fsm.process(FSMAction.PowerSuffix)
        fsm.process(FSMAction.Resolve)
        assertEquals(FSMState.Result, fsm.state)
    }
}
