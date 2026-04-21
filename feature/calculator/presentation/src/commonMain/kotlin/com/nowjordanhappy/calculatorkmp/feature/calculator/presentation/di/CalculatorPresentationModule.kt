package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.di

import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.CalculatorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val calculatorPresentationModule = module {
    viewModelOf(::CalculatorViewModel)
}
