package com.nowjordanhappy.calculatorkmp.core.domain.fsm

class ScientificStrategy : CalculatorStrategy {
    private val basic = BasicStrategy()

    override fun transition(
        state: FSMState,
        action: FSMAction,
    ): FSMTransition =
        when (action) {
            is FSMAction.Function ->
                when (state) {
                    FSMState.Empty,
                    FSMState.OperatorEntered,
                    FSMState.SecondOperand,
                    FSMState.Result, -> FSMTransition.Allow(FSMState.SecondOperand)
                    else -> FSMTransition.Block
                }
            is FSMAction.OpenParen ->
                when (state) {
                    FSMState.Empty,
                    FSMState.OperatorEntered,
                    FSMState.SecondOperand,
                    FSMState.Result, -> FSMTransition.Allow(FSMState.SecondOperand)
                    else -> FSMTransition.Block
                }
            is FSMAction.CloseParen ->
                when (state) {
                    FSMState.FirstOperand,
                    FSMState.SecondOperand, -> FSMTransition.Allow(FSMState.SecondOperand)
                    else -> FSMTransition.Block
                }
            is FSMAction.Constant ->
                when (state) {
                    FSMState.Empty,
                    FSMState.Result, -> FSMTransition.Allow(FSMState.FirstOperand)
                    FSMState.OperatorEntered,
                    FSMState.SecondOperand, -> FSMTransition.Allow(FSMState.SecondOperand)
                    else -> FSMTransition.Block
                }
            is FSMAction.PowerSuffix ->
                when (state) {
                    FSMState.FirstOperand -> FSMTransition.Allow(FSMState.FirstOperand)
                    FSMState.SecondOperand -> FSMTransition.Allow(FSMState.SecondOperand)
                    else -> FSMTransition.Block
                }
            else -> basic.transition(state, action)
        }
}
