package com.jordanrojas.calculatorkmp.di

import com.jordanrojas.calculatorkmp.core.domain.di.domainModule
import com.jordanrojas.calculatorkmp.feature.calculator.presentation.di.calculatorPresentationModule
import org.koin.dsl.module

val appModules = listOf(
    domainModule,
    calculatorPresentationModule
)
