package com.example.client.data.remote.security

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.client.data.repositories.SecurePreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideCryptoHelper(@ApplicationContext context: Context): CryptoHelper {
        return CryptoHelper(context)
    }

    @Provides
    @Singleton
    fun provideSecurePreferencesRepository(
        dataStore: DataStore<Preferences>,
        cryptoHelper: CryptoHelper
    ): SecurePreferencesRepository {
        return SecurePreferencesRepository(dataStore, cryptoHelper)
    }
}