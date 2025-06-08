package com.example.client.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

object PreferencesKeys {
    val USERNAME = stringPreferencesKey("username")

        val DARK_THEME = booleanPreferencesKey("dark_theme")

}

class PreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    val username: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USERNAME]
        }

    suspend fun saveUsername (username: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
        }
    }
}