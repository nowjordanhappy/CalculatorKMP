package com.jordanrojas.calculatorkmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.jordanrojas.calculatorkmp.di.appModules
import org.koin.core.context.startKoin

fun main() {
    startKoin { modules(appModules) }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CalculatorKMP"
        ) {
            App()
        }
    }
}
