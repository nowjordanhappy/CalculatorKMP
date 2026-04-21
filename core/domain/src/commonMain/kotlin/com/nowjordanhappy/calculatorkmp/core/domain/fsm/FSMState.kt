package com.nowjordanhappy.calculatorkmp.core.domain.fsm

enum class FSMState {
    Empty,
    FirstOperand,
    OperatorEntered,
    SecondOperand,
    Result,
    Error
}
