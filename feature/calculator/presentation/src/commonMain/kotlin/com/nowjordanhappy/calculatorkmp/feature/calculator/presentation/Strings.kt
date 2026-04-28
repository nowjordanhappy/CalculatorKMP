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

val FrStrings =
    Strings(
        buttonDelete = "Supprimer",
        buttonToggleSign = "Changer le signe",
        buttonPercent = "Pourcentage",
        buttonEquals = "Égal",
        buttonClear = "Effacer",
        buttonSquareRoot = "Racine carrée",
        buttonSquare = "Carré",
        buttonReciprocal = "Réciproque",
        buttonPower = "Puissance",
        buttonInverseSin = "Arcsinus",
        buttonInverseCos = "Arccosinus",
        buttonInverseTan = "Arctangente",
        buttonOpenParen = "Parenthèse ouvrante",
        buttonCloseParen = "Parenthèse fermante",
        buttonPi = "Pi",
        buttonE = "Nombre d'Euler",
        modeBasic = "Basique",
        modeScientific = "Scientifique",
        menuContentDescription = "Menu",
        themeSystem = "Thème système",
        themeLight = "Thème clair",
        themeDark = "Thème sombre",
        displayExpression = "Expression",
        displayResult = "Résultat",
    )

val DeStrings =
    Strings(
        buttonDelete = "Löschen",
        buttonToggleSign = "Vorzeichen wechseln",
        buttonPercent = "Prozent",
        buttonEquals = "Gleich",
        buttonClear = "Zurücksetzen",
        buttonSquareRoot = "Quadratwurzel",
        buttonSquare = "Quadrat",
        buttonReciprocal = "Kehrwert",
        buttonPower = "Potenz",
        buttonInverseSin = "Arkussinus",
        buttonInverseCos = "Arkuskosinus",
        buttonInverseTan = "Arkustangens",
        buttonOpenParen = "Klammer auf",
        buttonCloseParen = "Klammer zu",
        buttonPi = "Pi",
        buttonE = "Eulersche Zahl",
        modeBasic = "Einfach",
        modeScientific = "Wissenschaftlich",
        menuContentDescription = "Menü",
        themeSystem = "Systemdesign",
        themeLight = "Helles Design",
        themeDark = "Dunkles Design",
        displayExpression = "Ausdruck",
        displayResult = "Ergebnis",
    )

val PtStrings =
    Strings(
        buttonDelete = "Apagar",
        buttonToggleSign = "Alternar sinal",
        buttonPercent = "Porcentagem",
        buttonEquals = "Igual",
        buttonClear = "Limpar",
        buttonSquareRoot = "Raiz quadrada",
        buttonSquare = "Quadrado",
        buttonReciprocal = "Recíproco",
        buttonPower = "Potência",
        buttonInverseSin = "Arco seno",
        buttonInverseCos = "Arco cosseno",
        buttonInverseTan = "Arco tangente",
        buttonOpenParen = "Parêntese aberto",
        buttonCloseParen = "Parêntese fechado",
        buttonPi = "Pi",
        buttonE = "Número de Euler",
        modeBasic = "Básico",
        modeScientific = "Científico",
        menuContentDescription = "Menu",
        themeSystem = "Tema do sistema",
        themeLight = "Tema claro",
        themeDark = "Tema escuro",
        displayExpression = "Expressão",
        displayResult = "Resultado",
    )

val ItStrings =
    Strings(
        buttonDelete = "Elimina",
        buttonToggleSign = "Cambia segno",
        buttonPercent = "Percentuale",
        buttonEquals = "Uguale",
        buttonClear = "Cancella",
        buttonSquareRoot = "Radice quadrata",
        buttonSquare = "Quadrato",
        buttonReciprocal = "Reciproco",
        buttonPower = "Potenza",
        buttonInverseSin = "Arcoseno",
        buttonInverseCos = "Arcocoseno",
        buttonInverseTan = "Arcotangente",
        buttonOpenParen = "Parentesi aperta",
        buttonCloseParen = "Parentesi chiusa",
        buttonPi = "Pi",
        buttonE = "Numero di Eulero",
        modeBasic = "Base",
        modeScientific = "Scientifico",
        menuContentDescription = "Menu",
        themeSystem = "Tema di sistema",
        themeLight = "Tema chiaro",
        themeDark = "Tema scuro",
        displayExpression = "Espressione",
        displayResult = "Risultato",
    )

val LocalStrings = staticCompositionLocalOf { EnStrings }

fun getStrings(languageTag: String): Strings =
    when {
        languageTag.startsWith("fr") -> FrStrings
        languageTag.startsWith("de") -> DeStrings
        languageTag.startsWith("pt") -> PtStrings
        languageTag.startsWith("it") -> ItStrings
        languageTag.startsWith("es") -> EsStrings
        else -> EnStrings
    }
