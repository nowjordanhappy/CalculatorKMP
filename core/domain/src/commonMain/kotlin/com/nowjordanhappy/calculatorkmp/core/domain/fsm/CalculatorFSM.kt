package com.nowjordanhappy.calculatorkmp.core.domain.fsm

import com.nowjordanhappy.calculatorkmp.core.domain.Constants
import com.nowjordanhappy.calculatorkmp.core.domain.Operations

class CalculatorFSM(private val strategy: CalculatorStrategy) {
    var state: FSMState = FSMState.Empty
        private set

    fun process(action: FSMAction): FSMTransition {
        val transition = strategy.transition(state, action)
        if (transition is FSMTransition.Allow) {
            state = transition.nextState
        }
        return transition
    }

    fun reset() {
        state = FSMState.Empty
    }

    fun syncFromExpression(expression: String) {
        state =
            when {
                expression.isEmpty() -> FSMState.Empty
                else -> {
                    val last = expression.last().toString()
                    val endsWithOp =
                        last == Constants.OPERATOR_MULTI ||
                            last == Constants.OPERATOR_DIV ||
                            last == Constants.OPERATOR_SUM ||
                            last == Constants.OPERATOR_SUB
                    when {
                        endsWithOp -> {
                            if (last == Constants.OPERATOR_SUB) {
                                val prev = expression.getOrNull(expression.length - 2)?.toString()
                                val prevIsOp =
                                    prev != null &&
                                        (prev == Constants.OPERATOR_MULTI ||
                                            prev == Constants.OPERATOR_DIV ||
                                            prev == Constants.OPERATOR_SUM ||
                                            prev == Constants.OPERATOR_SUB)
                                if (prevIsOp) FSMState.SecondOperand else FSMState.OperatorEntered
                            } else {
                                FSMState.OperatorEntered
                            }
                        }
                        Operations.lastNumberSegment(expression) == expression -> FSMState.FirstOperand
                        else -> FSMState.SecondOperand
                    }
                }
            }
    }
}
