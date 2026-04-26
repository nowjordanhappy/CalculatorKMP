package com.nowjordanhappy.calculatorkmp.settings

import com.nowjordanhappy.calculatorkmp.core.domain.ThemeMode
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {
    private val settings = Settings()

    private val _themeMode =
        MutableStateFlow(
            ThemeMode.entries.getOrElse(settings.getInt(KEY_THEME, ThemeMode.SYSTEM.ordinal)) { ThemeMode.SYSTEM }
        )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setTheme(mode: ThemeMode) {
        settings.putInt(KEY_THEME, mode.ordinal)
        _themeMode.value = mode
    }

    companion object {
        private const val KEY_THEME = "theme_mode"
    }
}
