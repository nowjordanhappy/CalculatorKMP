package com.nowjordanhappy.calculatorkmp.di

import com.nowjordanhappy.calculatorkmp.core.domain.di.domainModule
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.di.calculatorPresentationModule

val appModules = listOf(domainModule, calculatorPresentationModule)
