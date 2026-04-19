package com.jordanrojas.calculatorkmp

import androidx.compose.runtime.Composable
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.CalculatorScreenRoot
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.LayoutConfig

@Composable
fun App(
    onIsScientificChanged: (Boolean) -> Unit = {},
    forceWide: Boolean = false,
    layoutConfig: LayoutConfig? = null
) {
    CalculatorTheme {
        CalculatorScreenRoot(
            onIsScientificChanged = onIsScientificChanged,
            forceWide = forceWide,
            layoutConfig = layoutConfig
        )
    }
}
