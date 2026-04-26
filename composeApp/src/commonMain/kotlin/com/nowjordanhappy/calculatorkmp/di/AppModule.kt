package com.nowjordanhappy.calculatorkmp.di

import com.nowjordanhappy.calculatorkmp.core.domain.di.domainModule
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.di.calculatorPresentationModule
import com.nowjordanhappy.calculatorkmp.settings.SettingsRepository
import org.koin.dsl.module

private val settingsModule = module { single { SettingsRepository() } }

val appModules = listOf(domainModule, calculatorPresentationModule, settingsModule)
