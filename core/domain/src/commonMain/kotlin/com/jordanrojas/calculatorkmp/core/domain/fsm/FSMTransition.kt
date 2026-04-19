package com.jordanrojas.calculatorkmp.core.domain.fsm

sealed class FSMTransition {
    data class Allow(val nextState: FSMState) : FSMTransition()
    object Block : FSMTransition()
}
