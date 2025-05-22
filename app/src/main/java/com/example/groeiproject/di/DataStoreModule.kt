package com.example.groeiproject.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.groeiproject.data.SettingsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private const val SETTINGS_NAME = "app_preferences"

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext ctx: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { ctx.preferencesDataStoreFile(SETTINGS_NAME) }
    )

    @Provides
    @Singleton
    fun provideSettingsManager(
        dataStore: DataStore<Preferences>
    ): SettingsManager = SettingsManager(dataStore)
}
