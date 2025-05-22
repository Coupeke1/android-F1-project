package com.example.groeiproject.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groeiproject.data.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val autoLandscape: StateFlow<Boolean> =
        settingsManager.autoLandscapeFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialValue = false
        )

    val showSponsors: StateFlow<Boolean> =
        settingsManager.showSponsorsFlow.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialValue = true
        )

    fun toggleAutoLandscape() = viewModelScope.launch {
        settingsManager.setAutoLandscape(!autoLandscape.value)
    }

    fun toggleShowSponsors() = viewModelScope.launch {
        settingsManager.setShowSponsors(!showSponsors.value)
    }
}