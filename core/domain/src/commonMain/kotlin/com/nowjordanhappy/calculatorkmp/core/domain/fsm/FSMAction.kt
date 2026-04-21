package com.nowjordanhappy.calculatorkmp.core.domain.fsm

sealed class FSMAction {
    data class Digit(val value: String) : FSMAction()
    data class Operator(val value: String) : FSMAction()
    object Point : FSMAction()
    object Percent : FSMAction()
    object SignToggle : FSMAction()
    object Resolve : FSMAction()
    object Delete : FSMAction()
    object Clear : FSMAction()
}
