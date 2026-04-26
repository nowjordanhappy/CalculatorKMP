package com.nowjordanhappy.calculatorkmp

import com.nowjordanhappy.calculatorkmp.core.domain.ThemeMode

data class AppState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isScientific: Boolean = false,
)
