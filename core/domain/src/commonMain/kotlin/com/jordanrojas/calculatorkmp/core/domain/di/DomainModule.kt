package com.jordanrojas.calculatorkmp.core.domain.di

import com.jordanrojas.calculatorkmp.core.domain.CalculatorUtils
import org.koin.dsl.module

val domainModule = module {
    factory { CalculatorUtils() }
}
