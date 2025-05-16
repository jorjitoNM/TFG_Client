package com.example.client.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

object PreferencesKeys {
    val USER_ID = stringPreferencesKey("user_id")
}

class PreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    val userId: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID] ?: UUID.randomUUID().toString()
        }

    suspend fun saveUsername (userId: UUID) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId.toString()
        }
    }
}