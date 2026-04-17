package com.jordanrojas.calculatorkmp.feature.calculator.presentation

import androidx.lifecycle.ViewModel
import com.jordanrojas.calculatorkmp.core.domain.CalculatorUtils
import com.jordanrojas.calculatorkmp.core.domain.Constants
import com.jordanrojas.calculatorkmp.core.domain.OperationResult
import com.jordanrojas.calculatorkmp.core.domain.Operations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel(private val calculatorUtils: CalculatorUtils) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.OnNumberClick -> appendToExpression(action.number)
            is CalculatorAction.OnOperatorClick -> handleOperator(action.operator)
            CalculatorAction.OnPointClick -> handlePoint()
            CalculatorAction.OnDeleteClick -> handleDelete()
            CalculatorAction.OnClearClick -> _state.update { CalculatorState() }
            CalculatorAction.OnResolveClick -> handleResolve()
            CalculatorAction.OnPercentClick -> handlePercent()
            CalculatorAction.OnSignToggleClick -> handleSignToggle()
            CalculatorAction.OnErrorDismiss -> _state.update { it.copy(error = null) }
        }
    }

    private fun appendToExpression(value: String) {
        val newExpression = _state.value.expression + value
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
        if (!calculatorUtils.addOperator(operator, current)) return

        val lastChar = current.lastOrNull()?.toString()
        val endsWithOperator = lastChar in listOf(
            Constants.OPERATOR_MULTI, Constants.OPERATOR_DIV,
            Constants.OPERATOR_SUM, Constants.OPERATOR_SUB
        )
        val newExpression = if (endsWithOperator && operator != Constants.OPERATOR_SUB) {
            current.dropLast(1) + operator
        } else {
            current + operator
        }
        _state.update { it.copy(expression = newExpression, result = "", error = null) }
    }

    private fun handlePoint() {
        if (!calculatorUtils.addPoint(_state.value.expression)) return
        _state.update { it.copy(expression = it.expression + Constants.POINT, error = null) }
    }

    private fun handleDelete() {
        val expr = _state.value.expression
        if (expr.isEmpty()) return
        _state.update { it.copy(expression = expr.dropLast(1), error = null) }
    }

    private fun handleResolve() {
        when (val result = calculatorUtils.checkOrResolve(_state.value.expression, true)) {
            is OperationResult.Success -> _state.update {
                it.copy(expression = formatResult(result.value), result = "", error = null)
            }
            is OperationResult.Error -> _state.update { it.copy(error = result.error) }
            OperationResult.NoOp -> Unit
        }
    }

    internal fun handlePercent() {
        val expr = _state.value.expression
        if (expr.isEmpty()) return
        val last = Operations.lastNumberSegment(expr)
        val value = last.toDoubleOrNull() ?: return
        val newLast = formatResult(value / 100)
        val newExpression = expr.dropLast(last.length) + newLast
        updateExpressionWithPreview(newExpression)
    }

    internal fun handleSignToggle() {
        val expr = _state.value.expression
        if (expr.isEmpty()) return
        val last = Operations.lastNumberSegment(expr)
        if (last.isEmpty()) return
        val newLast = if (last.startsWith("-")) last.drop(1) else "-$last"
        val newExpression = expr.dropLast(last.length) + newLast
        updateExpressionWithPreview(newExpression)
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
        return value.toBigDecimal().stripTrailingZeros().toPlainString()
    }
}
