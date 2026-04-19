package com.jordanrojas.calculatorkmp.feature.calculator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jordanrojas.calculatorkmp.core.domain.CalculatorError
import com.jordanrojas.calculatorkmp.core.domain.CalculatorUtils
import com.jordanrojas.calculatorkmp.core.domain.Constants
import com.jordanrojas.calculatorkmp.core.domain.OperationResult
import com.jordanrojas.calculatorkmp.core.domain.Operations
import com.jordanrojas.calculatorkmp.core.domain.fsm.BasicStrategy
import com.jordanrojas.calculatorkmp.core.domain.fsm.CalculatorFSM
import com.jordanrojas.calculatorkmp.core.domain.fsm.FSMAction
import com.jordanrojas.calculatorkmp.core.domain.fsm.FSMState
import com.jordanrojas.calculatorkmp.core.domain.fsm.FSMTransition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(private val calculatorUtils: CalculatorUtils) : ViewModel() {

    private val fsm = CalculatorFSM(BasicStrategy())
    private val _state = MutableStateFlow(CalculatorState())
    val state = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.OnNumberClick -> appendToExpression(action.number)
            is CalculatorAction.OnOperatorClick -> handleOperator(action.operator)
            CalculatorAction.OnPointClick -> handlePoint()
            CalculatorAction.OnDeleteClick -> handleDelete()
            CalculatorAction.OnClearClick -> handleClear()
            CalculatorAction.OnResolveClick -> handleResolve()
            CalculatorAction.OnPercentClick -> handlePercent()
            CalculatorAction.OnSignToggleClick -> handleSignToggle()
            CalculatorAction.OnErrorDismiss -> handleErrorDismiss()
            CalculatorAction.OnScientificToggle -> handleScienficToggle()
            CalculatorAction.OnDegRadToggle -> _state.update { it.copy(isRad = !it.isRad) }
            is CalculatorAction.OnScientificFunction -> Unit
        }
    }

    private fun handleScienficToggle() {
        //viewModelScope.launch {
            //delay(200L)
            _state.update {
                it.copy(isScientific = !it.isScientific)
            }
        //}
    }


    private fun appendToExpression(value: String) {
        val wasResult = fsm.state == FSMState.Result
        if (fsm.process(FSMAction.Digit(value)) is FSMTransition.Block) return

        val current = if (wasResult) "" else _state.value.expression
        val newExpression = when {
            current == "0" && value != "0" -> value
            current == "0" && value == "0" -> current
            current.isNotEmpty() && current.last().toString() in listOf(
                Constants.OPERATOR_MULTI, Constants.OPERATOR_DIV,
                Constants.OPERATOR_SUM, Constants.OPERATOR_SUB
            ) && value == "0" -> current + value
            current.length >= 2 && current.last().toString() == "0" && current[current.length - 2].toString() in listOf(
                Constants.OPERATOR_MULTI, Constants.OPERATOR_DIV,
                Constants.OPERATOR_SUM, Constants.OPERATOR_SUB
            ) && value != "0" -> current.dropLast(1) + value
            else -> current + value
        }
        val preview = calculatorUtils.checkOrResolve(newExpression, false)
        _state.update { state ->
            state.copy(
                expression = newExpression,
                result = if (preview is OperationResult.Success) formatResult(preview.value) else state.result,
                error = null
            )
        }
    }

    private fun handleOperator(operator: String) {
        val current = _state.value.expression
        if (current.endsWith(Constants.POINT)) return
        if (operator == Constants.OPERATOR_SUB && current.endsWith(Constants.OPERATOR_SUB)) return

        if (fsm.process(FSMAction.Operator(operator)) is FSMTransition.Block) return

        val lastChar = current.lastOrNull()?.toString()
        val endsWithOp = lastChar in listOf(
            Constants.OPERATOR_MULTI, Constants.OPERATOR_DIV,
            Constants.OPERATOR_SUM, Constants.OPERATOR_SUB
        )
        val newExpression = if (endsWithOp && operator != Constants.OPERATOR_SUB) {
            current.dropLast(1) + operator
        } else {
            current + operator
        }
        _state.update { it.copy(expression = newExpression, result = "", error = null) }
    }

    private fun handlePoint() {
        if (!calculatorUtils.addPoint(_state.value.expression)) return
        if (fsm.process(FSMAction.Point) is FSMTransition.Block) return
        _state.update { it.copy(expression = it.expression + Constants.POINT, error = null) }
    }

    private fun handleDelete() {
        val expr = _state.value.expression
        if (expr.isEmpty()) return
        if (fsm.process(FSMAction.Delete) is FSMTransition.Block) return
        val newExpr = expr.dropLast(1)
        fsm.syncFromExpression(newExpr)
        _state.update { it.copy(expression = newExpr, error = null) }
    }

    private fun handleClear() {
        fsm.reset()
        _state.update { CalculatorState() }
    }

    private fun handleResolve() {
        val prevState = fsm.state
        if (fsm.process(FSMAction.Resolve) is FSMTransition.Block) return

        when (fsm.state) {
            FSMState.Error -> _state.update { it.copy(error = CalculatorError.INCORRECT_EXPRESSION) }
            FSMState.Result -> {
                if (prevState == FSMState.Result) return
                when (val result = calculatorUtils.checkOrResolve(_state.value.expression, true)) {
                    is OperationResult.Success -> _state.update {
                        it.copy(expression = formatResult(result.value), result = "", error = null)
                    }
                    is OperationResult.Error -> {
                        fsm.syncFromExpression(_state.value.expression)
                        _state.update { it.copy(error = result.error) }
                    }
                    OperationResult.NoOp -> Unit
                }
            }
            else -> Unit
        }
    }

    internal fun handlePercent() {
        if (fsm.process(FSMAction.Percent) is FSMTransition.Block) return
        val expr = _state.value.expression
        val last = Operations.lastNumberSegment(expr)
        val value = last.toDoubleOrNull() ?: return
        val prefix = expr.dropLast(last.length)
        val newLast = if (prefix.isNotEmpty()) {
            val lastOp = prefix.last().toString()
            if (lastOp == Constants.OPERATOR_SUM || lastOp == Constants.OPERATOR_SUB) {
                val baseExpr = prefix.dropLast(1)
                val baseValue = when (val r = calculatorUtils.checkOrResolve(baseExpr, false)) {
                    is OperationResult.Success -> r.value
                    else -> baseExpr.toDoubleOrNull()
                }
                if (baseValue != null) formatResult(baseValue * value / 100) else formatResult(value / 100)
            } else {
                formatResult(value / 100)
            }
        } else {
            formatResult(value / 100)
        }
        val newExpression = prefix + newLast
        updateExpressionWithPreview(newExpression)
    }

    internal fun handleSignToggle() {
        if (fsm.process(FSMAction.SignToggle) is FSMTransition.Block) return
        val expr = _state.value.expression
        val last = Operations.lastNumberSegment(expr)
        if (last.isEmpty()) return
        val newLast = if (last.startsWith("-")) last.drop(1) else "-$last"
        val newExpression = expr.dropLast(last.length) + newLast
        updateExpressionWithPreview(newExpression)
    }

    private fun handleErrorDismiss() {
        fsm.syncFromExpression(_state.value.expression)
        _state.update { it.copy(error = null) }
    }

    private fun updateExpressionWithPreview(newExpression: String) {
        val preview = calculatorUtils.checkOrResolve(newExpression, false)
        _state.update {
            it.copy(
                expression = newExpression,
                result = if (preview is OperationResult.Success) formatResult(preview.value) else "",
                error = null
            )
        }
    }

    internal fun formatResult(value: Double): String {
        if (value == value.toLong().toDouble()) return value.toLong().toString()
        val str = value.toString()
        return if ('E' in str || 'e' in str) plainString(str) else str.trimEnd('0').trimEnd('.')
    }

    private fun plainString(scientific: String): String {
        val eIndex = scientific.indexOfFirst { it == 'E' || it == 'e' }
        val negative = scientific.startsWith("-")
        val base = scientific.substring(if (negative) 1 else 0, eIndex)
        val exp = scientific.substring(eIndex + 1).toInt()
        val digits = base.replace(".", "")
        val dotPos = base.indexOf('.').let { if (it == -1) base.length else it }
        val newDotPos = dotPos + exp
        val plain = when {
            newDotPos <= 0 -> "0." + "0".repeat(-newDotPos) + digits
            newDotPos >= digits.length -> digits + "0".repeat(newDotPos - digits.length)
            else -> digits.substring(0, newDotPos) + "." + digits.substring(newDotPos)
        }
        return ((if (negative) "-" else "") + plain).trimEnd('0').trimEnd('.')
    }
}
