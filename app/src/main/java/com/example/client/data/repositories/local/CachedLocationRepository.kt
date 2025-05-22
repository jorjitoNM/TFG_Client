package com.example.client.data.repositories.local

import com.example.client.common.NetworkResult
import com.example.client.data.local.dao.LocationDao
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
    suspend fun insertLocation (location : Location, userLogged : String) =withContext(dispatcher) {
        try {
            locationDao.insertLocation(location.toEntity(userLogged = userLogged, timestamp = System.currentTimeMillis()))
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
    suspend fun getRecentsLocations(userLogged: String) = withContext(dispatcher) {
        try {
           val locations = locationDao.getLocations(userLogged).map { it.toLocation() }
            NetworkResult.Success(locations)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun deleteLocation(id: Int, userLogged: String) = withContext(dispatcher) {
        try {
            locationDao.deleteLocation(id, userLogged)
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
}