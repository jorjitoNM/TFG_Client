package com.example.client.data.remote.security

import com.example.client.data.repositories.DataStoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            dataStoreRepository.getLoginToken().first()
        }
        val request = chain.request().newBuilder()
        if (chain.request().headers["Authorization"] == null)
            request.addHeader("Authorization", "Bearer $token")
        return chain.proceed(request.build())
    }
}