package com.example.client.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

object PreferencesKeys {
    val USER_ID = intPreferencesKey("user_id")
}

class PreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    val userId: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID] ?: -1
        }

    suspend fun saveUserId(id: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = id
        }
    }
}