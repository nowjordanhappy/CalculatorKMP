package com.nowjordanhappy.calculatorkmp.settings

import com.nowjordanhappy.calculatorkmp.core.domain.ThemeMode
import com.russhwolf.settings.Settings

class SettingsRepository {
    private val settings = Settings()

    val themeMode: ThemeMode
        get() = ThemeMode.entries.getOrElse(settings.getInt(KEY_THEME, ThemeMode.SYSTEM.ordinal)) { ThemeMode.SYSTEM }

    fun setTheme(mode: ThemeMode) {
        settings.putInt(KEY_THEME, mode.ordinal)
    }

    val isScientific: Boolean
        get() = settings.getBoolean(KEY_SCIENTIFIC, false)

    fun toggleScientific(): Boolean {
        val next = !isScientific
        settings.putBoolean(KEY_SCIENTIFIC, next)
        return next
    }

    companion object {
        private const val KEY_THEME = "theme_mode"
        private const val KEY_SCIENTIFIC = "is_scientific"
    }
}
