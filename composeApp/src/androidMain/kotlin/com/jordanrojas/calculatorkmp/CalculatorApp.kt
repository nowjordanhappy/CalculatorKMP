package com.jordanrojas.calculatorkmp

import android.app.Application
import com.jordanrojas.calculatorkmp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CalculatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CalculatorApp)
            modules(appModules)
        }
    }
}
