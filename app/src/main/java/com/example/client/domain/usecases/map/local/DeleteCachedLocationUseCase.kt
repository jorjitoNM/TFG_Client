package com.example.client.domain.usecases.map.local

import com.example.client.data.repositories.local.CachedLocationRepository
import javax.inject.Inject

class DeleteCachedLocationUseCase @Inject constructor(private val cachedLocationRepository: CachedLocationRepository) {
    suspend operator fun invoke(id : Int, userLogged : String) = cachedLocationRepository.deleteLocation(id, userLogged)
}