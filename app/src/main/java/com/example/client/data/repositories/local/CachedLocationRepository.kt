package com.example.client.data.repositories.local

import com.example.client.common.NetworkResult
import com.example.client.data.local.dao.LocationDao
import com.example.client.data.local.entities.LocationEntity
import com.example.client.data.local.entities.toEntity
import com.example.client.data.local.entities.toLocation
import com.example.client.di.IoDispatcher
import com.example.client.domain.model.google.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CachedLocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
){
    suspend fun insertLocation (location : Location) =withContext(dispatcher) {
        try {
            locationDao.insertLocation(location.toEntity())
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
    suspend fun getLocations() = withContext(dispatcher) {
        try {
           val locations = locationDao.getLocations().map { it.toLocation() }
            NetworkResult.Success(locations)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun getLocation(id: Int) = withContext(dispatcher) {
        try {
           val location = locationDao.getLocation(id).toLocation()
            NetworkResult.Success(location)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}