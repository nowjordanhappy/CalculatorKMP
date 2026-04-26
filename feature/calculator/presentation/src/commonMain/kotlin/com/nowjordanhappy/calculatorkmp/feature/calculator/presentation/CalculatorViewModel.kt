package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation

import androidx.lifecycle.ViewModel
import com.nowjordanhappy.calculatorkmp.core.domain.CalculatorError
import com.nowjordanhappy.calculatorkmp.core.domain.Constants
import com.nowjordanhappy.calculatorkmp.core.domain.EvaluationResult
import com.nowjordanhappy.calculatorkmp.core.domain.ExpressionProcessor
import com.nowjordanhappy.calculatorkmp.core.domain.fsm.CalculatorFSM
import com.nowjordanhappy.calculatorkmp.core.domain.fsm.FSMAction
import com.nowjordanhappy.calculatorkmp.core.domain.fsm.FSMState
import com.nowjordanhappy.calculatorkmp.core.domain.fsm.FSMTransition
import com.nowjordanhappy.calculatorkmp.core.domain.fsm.ScientificStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val FUNCTION_PREFIXES = listOf("asin(", "acos(", "atan(", "sqrt(", "sin(", "cos(", "tan(", "log(", "ln(")

class CalculatorViewModel(private val processor: ExpressionProcessor) : ViewModel() {
    private val fsm = CalculatorFSM(ScientificStrategy())
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
            CalculatorAction.OnScientificToggle -> handleScientificToggle()
            CalculatorAction.OnDegRadToggle -> handleDegRadToggle()
            is CalculatorAction.OnScientificFunction -> handleScientificFunction(action.function)
        }
    }

    private fun handleScientificToggle() {
        _state.update { it.copy(isScientific = !it.isScientific) }
    }

    private fun appendToExpression(value: String) {
        val wasError = _state.value.error != null
        if (wasError) fsm.reset()
        val wasResult = fsm.state == FSMState.Result
        if (fsm.process(FSMAction.Digit(value)) is FSMTransition.Block) return

        val current = if (wasResult || wasError) "" else _state.value.expression
        val newExpression = processor.appendDigit(current, value)
        val preview = processor.evaluate(newExpression, false, _state.value.isRad)
        _state.update { state ->
            state.copy(
                expression = newExpression,
                result = if (preview is EvaluationResult.Success) formatDisplay(preview.value) else state.result,
                error = null,
                isAcMode = false,
            )
        }
    }

    private fun handleOperator(operator: String) {
        if (_state.value.error != null) return
        val raw = _state.value.expression
        val current = if (raw.endsWith(Constants.POINT)) raw.dropLast(1) else raw
        if (operator == Constants.OPERATOR_SUB && current.endsWith(Constants.OPERATOR_SUB)) return

        if (fsm.process(FSMAction.Operator(operator)) is FSMTransition.Block) return

        val lastChar = current.lastOrNull()?.toString()
        val endsWithOp = lastChar in Constants.BINARY_OPERATORS
        val newExpression =
            if (endsWithOp && operator != Constants.OPERATOR_SUB) {
                current.dropLast(1) + operator
            } else {
                current + operator
            }
        _state.update { it.copy(expression = newExpression, result = "", error = null, isAcMode = false) }
    }

    private fun handlePoint() {
        if (_state.value.error != null) return
        if (!processor.addPoint(_state.value.expression)) return
        if (fsm.process(FSMAction.Point) is FSMTransition.Block) return
        val base = if (_state.value.expression.isEmpty()) "0" else _state.value.expression
        _state.update { it.copy(expression = base + Constants.POINT, error = null, isAcMode = false) }
    }

    private fun handleDelete() {
        if (_state.value.error != null) return
        val expr = _state.value.expression
        if (expr.isEmpty()) return
        if (fsm.process(FSMAction.Delete) is FSMTransition.Block) return
        val prefix = FUNCTION_PREFIXES.firstOrNull { expr.endsWith(it) }
        val newExpr = if (prefix != null) expr.dropLast(prefix.length) else expr.dropLast(1)
        fsm.syncFromExpression(newExpr)
        _state.update { it.copy(expression = newExpr, error = null, isAcMode = newExpr.isEmpty()) }
    }

    private fun handleClear() {
        fsm.reset()
        _state.update { it.copy(expression = "", result = "", error = null, isAcMode = true) }
    }

    private fun handleResolve() {
        val prevState = fsm.state
        if (fsm.process(FSMAction.Resolve) is FSMTransition.Block) return

        when (fsm.state) {
            FSMState.Error -> _state.update { it.copy(error = CalculatorError.MATH_ERROR, isAcMode = true) }
            FSMState.Result -> {
                if (prevState == FSMState.Result) return
                val expr = _state.value.expression
                if (expr.endsWith(Constants.POINT)) {
                    _state.update { it.copy(expression = expr.dropLast(1), result = "", error = null, isAcMode = true) }
                    return
                }
                when (val result = processor.evaluate(expr, true, _state.value.isRad)) {
                    is EvaluationResult.Success ->
                        _state.update {
                            it.copy(
                                expression = processor.formatResult(result.value),
                                result = "",
                                error = null,
                                isAcMode = true,
                            )
                        }
                    is EvaluationResult.Error -> {
                        fsm.syncFromExpression(expr)
                        _state.update { it.copy(error = result.error, isAcMode = true) }
                    }
                    EvaluationResult.NoOp -> Unit
                }
            }
            else -> Unit
        }
    }

    internal fun handlePercent() {
        if (_state.value.error != null) return
        if (fsm.process(FSMAction.Percent) is FSMTransition.Block) return
        val newExpression = processor.applyPercent(_state.value.expression, _state.value.isRad) ?: return
        updateExpressionWithPreview(newExpression)
    }

    internal fun handleSignToggle() {
        if (_state.value.error != null) return
        if (fsm.process(FSMAction.SignToggle) is FSMTransition.Block) return
        val newExpression = processor.applySignToggle(_state.value.expression) ?: return
        updateExpressionWithPreview(newExpression)
    }

    private fun handleDegRadToggle() {
        val newIsRad = !_state.value.isRad
        val expr = _state.value.expression
        val preview = processor.evaluate(expr, false, newIsRad)
        _state.update {
            it.copy(
                isRad = newIsRad,
                result = if (preview is EvaluationResult.Success) formatDisplay(preview.value) else it.result,
            )
        }
    }

    private fun handleErrorDismiss() {
        fsm.syncFromExpression(_state.value.expression)
        _state.update { it.copy(error = null) }
    }

    private fun updateExpressionWithPreview(newExpression: String) {
        val preview = processor.evaluate(newExpression, false, _state.value.isRad)
        _state.update {
            it.copy(
                expression = newExpression,
                result = if (preview is EvaluationResult.Success) formatDisplay(preview.value) else "",
                error = null,
                isAcMode = false,
            )
        }
    }

    private fun handleScientificFunction(function: String) {
        val wasError = _state.value.error != null
        if (wasError) fsm.reset()

        val (fsmAction, fragment) =
            when (function) {
                ButtonLabels.Scientific.SIN -> FSMAction.Function to "sin("
                ButtonLabels.Scientific.COS -> FSMAction.Function to "cos("
                ButtonLabels.Scientific.TAN -> FSMAction.Function to "tan("
                ButtonLabels.Scientific.ASIN -> FSMAction.Function to "asin("
                ButtonLabels.Scientific.ACOS -> FSMAction.Function to "acos("
                ButtonLabels.Scientific.ATAN -> FSMAction.Function to "atan("
                ButtonLabels.Scientific.LN -> FSMAction.Function to "ln("
                ButtonLabels.Scientific.LOG -> FSMAction.Function to "log("
                ButtonLabels.Scientific.SQRT -> FSMAction.Function to "sqrt("
                ButtonLabels.Scientific.SQUARE -> FSMAction.PowerSuffix to "^2"
                ButtonLabels.Scientific.RECIPROCAL -> FSMAction.PowerSuffix to "^-1"
                ButtonLabels.Scientific.POWER -> FSMAction.Operator(Constants.OPERATOR_POWER) to "^"
                ButtonLabels.Scientific.PI -> FSMAction.Constant to "π"
                ButtonLabels.Scientific.E -> FSMAction.Constant to "e"
                ButtonLabels.Scientific.OPEN_PAREN -> FSMAction.OpenParen to "("
                ButtonLabels.Scientific.CLOSE_PAREN -> {
                    val expr = if (wasError) "" else _state.value.expression
                    val openCount = expr.count { it == '(' } - expr.count { it == ')' }
                    if (openCount <= 0) return
                    FSMAction.CloseParen to ")"
                }
                else -> return
            }

        val wasResult = fsm.state == FSMState.Result
        val current = if (wasResult || wasError) "" else _state.value.expression

        val implicitMultiply =
            when (fsmAction) {
                FSMAction.Function,
                FSMAction.OpenParen,
                FSMAction.Constant, -> processor.needsImplicitMultiply(current)
                else -> false
            }

        if (implicitMultiply) {
            if (fsm.process(FSMAction.Operator(Constants.OPERATOR_MULTI)) is FSMTransition.Block) return
        }
        if (fsm.process(fsmAction) is FSMTransition.Block) return

        val prefix = if (implicitMultiply) Constants.OPERATOR_MULTI else ""
        val newExpression = current + prefix + fragment

        val preview = processor.evaluate(newExpression, false, _state.value.isRad)
        _state.update { state ->
            state.copy(
                expression = newExpression,
                result = if (preview is EvaluationResult.Success) formatDisplay(preview.value) else state.result,
                error = null,
                isAcMode = false,
            )
        }
    }

    internal fun formatDisplay(value: Double): String {
        val abs = kotlin.math.abs(value)
        if (abs == 0.0 || (abs >= 1e-6 && abs < 1e10)) return processor.formatResult(value)
        val str = value.toString()
        val eIdx = str.indexOfFirst { it == 'E' || it == 'e' }
        if (eIdx == -1) return processor.formatResult(value)
        val neg = str.startsWith("-")
        val mantissa = str.substring(if (neg) 1 else 0, eIdx).trimEnd('0').trimEnd('.')
        val exp = str.substring(eIdx + 1).toInt()
        return "${if (neg) "-" else ""}${mantissa}E${exp}"
    }
}
