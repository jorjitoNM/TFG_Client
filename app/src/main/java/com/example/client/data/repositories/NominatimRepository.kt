package com.example.client.data.repositories

import com.example.client.common.NetworkResult
import com.example.client.data.remote.service.NominatimService
import com.example.client.domain.model.note.NominatimPlace
import com.example.musicapprest.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

    class NominatimRepository @Inject constructor(
        private val service: NominatimService,
        @IoDispatcher private val dispatcher: CoroutineDispatcher
    ) {


        suspend fun searchPlaces(query: String) = withContext(dispatcher) {
             try {
                val result = service.searchPlaces(query, "json", 1)
                NetworkResult.Success(result)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: e.toString())
            }
        }

    }
