package com.example.groeiproject.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsManager(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val AUTO_LANDSCAPE = booleanPreferencesKey("auto_landscape")
        val SHOW_SPONSORS = booleanPreferencesKey("show_sponsors")
    }

    val autoLandscapeFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[Keys.AUTO_LANDSCAPE] ?: false }

    val showSponsorsFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[Keys.SHOW_SPONSORS] ?: true }

    suspend fun setAutoLandscape(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.AUTO_LANDSCAPE] = enabled
        }
    }

    suspend fun setShowSponsors(show: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.SHOW_SPONSORS] = show
        }
    }
}
