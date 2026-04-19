package com.jordanrojas.calculatorkmp.core.domain.fsm

enum class FSMState {
    Empty,
    FirstOperand,
    OperatorEntered,
    SecondOperand,
    Result,
    Error
}
