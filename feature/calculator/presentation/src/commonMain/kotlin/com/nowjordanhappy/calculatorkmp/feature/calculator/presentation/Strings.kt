package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation

import androidx.compose.runtime.staticCompositionLocalOf

data class Strings(
    // Button content descriptions (accessibility)
    val buttonDelete: String,
    val buttonToggleSign: String,
    val buttonPercent: String,
    val buttonEquals: String,
    val buttonClear: String,
    val buttonSquareRoot: String,
    val buttonSquare: String,
    val buttonReciprocal: String,
    val buttonPower: String,
    val buttonInverseSin: String,
    val buttonInverseCos: String,
    val buttonInverseTan: String,
    val buttonOpenParen: String,
    val buttonCloseParen: String,
    val buttonPi: String,
    val buttonE: String,
    // UI text
    val modeBasic: String,
    val modeScientific: String,
    val menuContentDescription: String,
    val themeSystem: String,
    val themeLight: String,
    val themeDark: String,
    // Display semantics
    val displayExpression: String,
    val displayResult: String,
)

val EnStrings =
    Strings(
        buttonDelete = "Delete",
        buttonToggleSign = "Toggle sign",
        buttonPercent = "Percent",
        buttonEquals = "Equals",
        buttonClear = "Clear",
        buttonSquareRoot = "Square root",
        buttonSquare = "Square",
        buttonReciprocal = "Reciprocal",
        buttonPower = "Power",
        buttonInverseSin = "Inverse sine",
        buttonInverseCos = "Inverse cosine",
        buttonInverseTan = "Inverse tangent",
        buttonOpenParen = "Open parenthesis",
        buttonCloseParen = "Close parenthesis",
        buttonPi = "Pi",
        buttonE = "Euler's number",
        modeBasic = "Basic",
        modeScientific = "Scientific",
        menuContentDescription = "Menu",
        themeSystem = "System theme",
        themeLight = "Light theme",
        themeDark = "Dark theme",
        displayExpression = "Expression",
        displayResult = "Result",
    )

val EsStrings =
    Strings(
        buttonDelete = "Eliminar",
        buttonToggleSign = "Cambiar signo",
        buttonPercent = "Porcentaje",
        buttonEquals = "Igual",
        buttonClear = "Limpiar",
        buttonSquareRoot = "Raíz cuadrada",
        buttonSquare = "Cuadrado",
        buttonReciprocal = "Recíproco",
        buttonPower = "Potencia",
        buttonInverseSin = "Arcoseno",
        buttonInverseCos = "Arcocoseno",
        buttonInverseTan = "Arcotangente",
        buttonOpenParen = "Paréntesis abierto",
        buttonCloseParen = "Paréntesis cerrado",
        buttonPi = "Pi",
        buttonE = "Número de Euler",
        modeBasic = "Básico",
        modeScientific = "Científico",
        menuContentDescription = "Menú",
        themeSystem = "Tema del sistema",
        themeLight = "Tema claro",
        themeDark = "Tema oscuro",
        displayExpression = "Expresión",
        displayResult = "Resultado",
    )

val LocalStrings = staticCompositionLocalOf { EnStrings }

fun getStrings(languageTag: String): Strings =
    when {
        languageTag.startsWith("es") -> EsStrings
        else -> EnStrings
    }
