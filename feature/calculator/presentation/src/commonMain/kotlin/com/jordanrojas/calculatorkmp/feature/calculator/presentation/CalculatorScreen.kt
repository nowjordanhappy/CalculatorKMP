package com.jordanrojas.calculatorkmp.feature.calculator.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordanrojas.calculatorkmp.core.domain.CalculatorError
import com.jordanrojas.calculatorkmp.core.domain.Constants
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CalculatorScreenRoot(
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    CalculatorScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            if (state.error != null) {
                Text(
                    text = when (state.error) {
                        CalculatorError.INCORRECT_NUMBER -> "Incorrect number"
                        CalculatorError.INCORRECT_EXPRESSION -> "Incorrect expression"
                    },
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (state.result.isNotEmpty()) {
                val exprScrollState = rememberScrollState()
                LaunchedEffect(state.expression) { exprScrollState.animateScrollTo(exprScrollState.maxValue) }
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(exprScrollState),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = state.expression,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        softWrap = false
                    )
                }
                Text(
                    text = "= ${state.result}",
                    fontSize = adaptiveFontSize(state.result.length),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                val exprScrollState = rememberScrollState()
                LaunchedEffect(state.expression) { exprScrollState.animateScrollTo(exprScrollState.maxValue) }
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(exprScrollState),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = state.expression.ifEmpty { "0" },
                        fontSize = adaptiveFontSize(state.expression.length),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        softWrap = false
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Button grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CalcRow {
                CalcButton("C", ButtonType.Action) { onAction(CalculatorAction.OnClearClick) }
                CalcButton("+/-", ButtonType.Action) { onAction(CalculatorAction.OnSignToggleClick) }
                CalcButton("%", ButtonType.Action) { onAction(CalculatorAction.OnPercentClick) }
                CalcButton(Constants.OPERATOR_DIV, ButtonType.Operator) { onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_DIV)) }
            }
            CalcRow {
                CalcButton("7") { onAction(CalculatorAction.OnNumberClick("7")) }
                CalcButton("8") { onAction(CalculatorAction.OnNumberClick("8")) }
                CalcButton("9") { onAction(CalculatorAction.OnNumberClick("9")) }
                CalcButton(Constants.OPERATOR_MULTI, ButtonType.Operator) { onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_MULTI)) }
            }
            CalcRow {
                CalcButton("4") { onAction(CalculatorAction.OnNumberClick("4")) }
                CalcButton("5") { onAction(CalculatorAction.OnNumberClick("5")) }
                CalcButton("6") { onAction(CalculatorAction.OnNumberClick("6")) }
                CalcButton(Constants.OPERATOR_SUB, ButtonType.Operator) { onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_SUB)) }
            }
            CalcRow {
                CalcButton("1") { onAction(CalculatorAction.OnNumberClick("1")) }
                CalcButton("2") { onAction(CalculatorAction.OnNumberClick("2")) }
                CalcButton("3") { onAction(CalculatorAction.OnNumberClick("3")) }
                CalcButton(Constants.OPERATOR_SUM, ButtonType.Operator) { onAction(CalculatorAction.OnOperatorClick(Constants.OPERATOR_SUM)) }
            }
            CalcRow {
                CalcButton("⌫", ButtonType.Action) { onAction(CalculatorAction.OnDeleteClick) }
                CalcButton("0") { onAction(CalculatorAction.OnNumberClick("0")) }
                CalcButton(".") { onAction(CalculatorAction.OnPointClick) }
                CalcButton("=", ButtonType.Equals) { onAction(CalculatorAction.OnResolveClick) }
            }
        }
    }
}

private fun adaptiveFontSize(length: Int) = when {
    length > 12 -> 32.sp
    length > 9  -> 44.sp
    length > 6  -> 54.sp
    else        -> 64.sp
}

enum class ButtonType { Number, Operator, Action, Equals }

@Composable
private fun CalcRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun RowScope.CalcButton(
    text: String,
    type: ButtonType = ButtonType.Number,
    onClick: () -> Unit
) {
    val containerColor = when (type) {
        ButtonType.Operator, ButtonType.Equals -> MaterialTheme.colorScheme.primary
        ButtonType.Action -> MaterialTheme.colorScheme.secondary
        ButtonType.Number -> MaterialTheme.colorScheme.tertiary
    }
    val contentColor = when (type) {
        ButtonType.Operator, ButtonType.Equals -> MaterialTheme.colorScheme.onPrimary
        ButtonType.Action -> MaterialTheme.colorScheme.onSecondary
        ButtonType.Number -> MaterialTheme.colorScheme.onTertiary
    }
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
