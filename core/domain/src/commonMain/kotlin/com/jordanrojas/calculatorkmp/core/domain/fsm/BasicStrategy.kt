package com.jordanrojas.calculatorkmp.core.domain.fsm

import com.jordanrojas.calculatorkmp.core.domain.Constants

class BasicStrategy : CalculatorStrategy {

    override fun transition(state: FSMState, action: FSMAction): FSMTransition = when (state) {
        FSMState.Empty -> when (action) {
            is FSMAction.Digit -> FSMTransition.Allow(FSMState.FirstOperand)
            is FSMAction.Operator -> if (action.value == Constants.OPERATOR_SUB)
                FSMTransition.Allow(FSMState.FirstOperand)
            else FSMTransition.Block
            FSMAction.Clear -> FSMTransition.Allow(FSMState.Empty)
            else -> FSMTransition.Block
        }

        FSMState.FirstOperand -> when (action) {
            is FSMAction.Digit -> FSMTransition.Allow(FSMState.FirstOperand)
            is FSMAction.Operator -> FSMTransition.Allow(FSMState.OperatorEntered)
            FSMAction.Point -> FSMTransition.Allow(FSMState.FirstOperand)
            FSMAction.Percent -> FSMTransition.Allow(FSMState.FirstOperand)
            FSMAction.SignToggle -> FSMTransition.Allow(FSMState.FirstOperand)
            FSMAction.Resolve -> FSMTransition.Allow(FSMState.Result)
            FSMAction.Delete -> FSMTransition.Allow(FSMState.FirstOperand)
            FSMAction.Clear -> FSMTransition.Allow(FSMState.Empty)
        }

        FSMState.OperatorEntered -> when (action) {
            is FSMAction.Digit -> FSMTransition.Allow(FSMState.SecondOperand)
            is FSMAction.Operator -> if (action.value == Constants.OPERATOR_SUB)
                FSMTransition.Allow(FSMState.SecondOperand)
            else FSMTransition.Allow(FSMState.OperatorEntered)
            FSMAction.Delete -> FSMTransition.Allow(FSMState.FirstOperand)
            FSMAction.Clear -> FSMTransition.Allow(FSMState.Empty)
            FSMAction.Resolve -> FSMTransition.Allow(FSMState.Error)
            else -> FSMTransition.Block
        }

        FSMState.SecondOperand -> when (action) {
            is FSMAction.Digit -> FSMTransition.Allow(FSMState.SecondOperand)
            is FSMAction.Operator -> FSMTransition.Allow(FSMState.OperatorEntered)
            FSMAction.Point -> FSMTransition.Allow(FSMState.SecondOperand)
            FSMAction.Percent -> FSMTransition.Allow(FSMState.SecondOperand)
            FSMAction.SignToggle -> FSMTransition.Allow(FSMState.SecondOperand)
            FSMAction.Resolve -> FSMTransition.Allow(FSMState.Result)
            FSMAction.Delete -> FSMTransition.Allow(FSMState.SecondOperand)
            FSMAction.Clear -> FSMTransition.Allow(FSMState.Empty)
        }

        FSMState.Result -> when (action) {
            is FSMAction.Digit -> FSMTransition.Allow(FSMState.FirstOperand)
            is FSMAction.Operator -> FSMTransition.Allow(FSMState.OperatorEntered)
            FSMAction.Percent -> FSMTransition.Allow(FSMState.FirstOperand)
            FSMAction.SignToggle -> FSMTransition.Allow(FSMState.FirstOperand)
            FSMAction.Resolve -> FSMTransition.Allow(FSMState.Result)
            FSMAction.Delete -> FSMTransition.Allow(FSMState.FirstOperand)
            FSMAction.Clear -> FSMTransition.Allow(FSMState.Empty)
            FSMAction.Point -> FSMTransition.Block
        }

        FSMState.Error -> when (action) {
            FSMAction.Clear -> FSMTransition.Allow(FSMState.Empty)
            else -> FSMTransition.Block
        }
    }
}
