package com.jordanrojas.calculatorkmp

import androidx.compose.ui.window.ComposeUIViewController
import com.jordanrojas.calculatorkmp.di.appModules
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    App()
}

fun initKoinIos() {
    startKoin { modules(appModules) }
}
