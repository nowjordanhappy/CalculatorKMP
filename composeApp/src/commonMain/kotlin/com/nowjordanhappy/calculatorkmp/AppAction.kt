package com.nowjordanhappy.calculatorkmp

import com.nowjordanhappy.calculatorkmp.core.domain.ThemeMode

sealed interface AppAction {
    data class OnThemeChange(val mode: ThemeMode) : AppAction

    data object OnScientificToggle : AppAction
}
