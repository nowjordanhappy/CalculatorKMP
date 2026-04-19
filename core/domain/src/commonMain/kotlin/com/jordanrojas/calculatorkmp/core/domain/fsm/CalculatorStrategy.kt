package com.jordanrojas.calculatorkmp.core.domain.fsm

interface CalculatorStrategy {
    fun transition(state: FSMState, action: FSMAction): FSMTransition
}
