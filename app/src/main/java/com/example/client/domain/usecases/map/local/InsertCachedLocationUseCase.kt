package com.example.client.domain.usecases.map.local

import com.example.client.data.repositories.local.CachedLocationRepository
import com.example.client.domain.model.google.Location
import javax.inject.Inject

class InsertCachedLocationUseCase @Inject constructor(
    private val repository: CachedLocationRepository
) {
    suspend operator fun invoke(location: Location, userLogged: String) = repository.insertLocation(location, userLogged)
}