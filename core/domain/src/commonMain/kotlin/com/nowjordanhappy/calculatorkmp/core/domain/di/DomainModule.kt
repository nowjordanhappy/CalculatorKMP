package com.nowjordanhappy.calculatorkmp.core.domain.di

import com.nowjordanhappy.calculatorkmp.core.domain.ExpressionProcessor
import org.koin.dsl.module

val domainModule = module { factory { ExpressionProcessor() } }
