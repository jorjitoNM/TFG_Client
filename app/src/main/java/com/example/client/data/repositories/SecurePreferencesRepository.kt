package com.example.client.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.client.data.remote.security.CryptoHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cryptoHelper: CryptoHelper
) {
    companion object {
        private val ENCRYPTED_CREDENTIALS = stringPreferencesKey("encrypted_credentials_marker")
    }

    suspend fun saveCredentials(email: String, password: String) {
        val combined = "$email:$password"
        cryptoHelper.encryptAndSaveData(combined)
        dataStore.edit { preferences ->
            preferences[ENCRYPTED_CREDENTIALS] = "present"
        }
    }

    fun getCredentials(): Pair<String?, String?> {
        return try {
            val decrypted = cryptoHelper.readAndDecryptData()
            val parts = decrypted.split(":")
            if (parts.size == 2) Pair(parts[0], parts[1]) else Pair(null, null)
        } catch (e: Exception) {
            Pair(null, null)
        }
    }
}