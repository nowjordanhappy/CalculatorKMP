package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nowjordanhappy.calculatorkmp.core.domain.CalculatorError

@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    error: CalculatorError?,
    isAcMode: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        if (error != null) {
            Text(
                text =
                    when (error) {
                        CalculatorError.UNDEFINED -> "Undefined"
                        CalculatorError.MATH_ERROR -> "Math Error"
                    },
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
            )
            return@Column
        }
        if (result.isNotEmpty()) {
            val exprScrollState = rememberScrollState()
            LaunchedEffect(expression) { exprScrollState.animateScrollTo(exprScrollState.maxValue) }
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(exprScrollState),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = expression,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    softWrap = false,
                )
            }
            Text(
                text = "= $result",
                fontSize = adaptiveFontSize(result.length),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            val exprScrollState = rememberScrollState()
            LaunchedEffect(expression) {
                if (isAcMode) exprScrollState.scrollTo(0)
                else exprScrollState.animateScrollTo(exprScrollState.maxValue)
            }
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(exprScrollState),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = adaptiveFontSize(expression.length),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    softWrap = false,
                )
            }
        }
    }
}

fun adaptiveFontSize(length: Int) =
    when {
        length > 12 -> 32.sp
        length > 9 -> 44.sp
        length > 6 -> 54.sp
        else -> 64.sp
    }
