package com.jordanrojas.calculatorkmp

import androidx.compose.runtime.Composable
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.CalculatorScreenRoot

@Composable
fun App(
    onIsScientificChanged: (Boolean) -> Unit = {},
    forceWide: Boolean = false
) {
    CalculatorTheme {
        CalculatorScreenRoot(
            onIsScientificChanged = onIsScientificChanged,
            forceWide = forceWide
        )
    }
}
