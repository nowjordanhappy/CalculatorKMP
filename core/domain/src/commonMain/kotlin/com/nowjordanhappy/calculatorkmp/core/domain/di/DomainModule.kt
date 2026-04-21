package com.nowjordanhappy.calculatorkmp.core.domain.di

import com.nowjordanhappy.calculatorkmp.core.domain.CalculatorUtils
import org.koin.dsl.module

val domainModule = module {
    factory { CalculatorUtils() }
}
