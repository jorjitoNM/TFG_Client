package com.example.client.domain.usecases.map.local

import com.example.client.data.repositories.local.CachedLocationRepository
import javax.inject.Inject

class GetCachedLocationsUseCase @Inject constructor(
    private val repository: CachedLocationRepository
) {
    suspend operator fun invoke(userLogged : String) = repository.getRecentsLocations(userLogged)
}