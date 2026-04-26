package com.nowjordanhappy.calculatorkmp

import androidx.lifecycle.ViewModel
import com.nowjordanhappy.calculatorkmp.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(private val repository: SettingsRepository) : ViewModel() {
    private val _state = MutableStateFlow(
        AppState(
            themeMode = repository.themeMode,
            isScientific = repository.isScientific,
        )
    )
    val state = _state.asStateFlow()

    fun onAction(action: AppAction) {
        when (action) {
            is AppAction.OnThemeChange -> {
                repository.setTheme(action.mode)
                _state.update { it.copy(themeMode = action.mode) }
            }
            AppAction.OnScientificToggle -> {
                repository.toggleScientific()
                _state.update { it.copy(isScientific = !it.isScientific) }
            }
        }
    }
}
