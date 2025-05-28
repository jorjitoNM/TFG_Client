package com.example.client.data.remote.security

import com.example.client.data.remote.service.AuthenticationService
import com.example.client.data.repositories.DataStoreRepository
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val service : Lazy<AuthenticationService>,
): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking {
            dataStoreRepository.getRefreshToken().first()
        }
        return runBlocking {
            val newToken = getNewToken(refreshToken)
            newToken.body()?.let {
                dataStoreRepository.saveAccessToken(it)
                response.request.newBuilder()
                    .header("Authorization", "Bearer $it")
                    .build()
            }
        }
    }

    private suspend fun getNewToken(refreshToken: String?): retrofit2.Response<String> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return service.get().refreshToken("Bearer $refreshToken")
    }
}
