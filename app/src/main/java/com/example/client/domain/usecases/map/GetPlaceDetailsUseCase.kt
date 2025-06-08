package com.example.client.domain.usecases.map

import com.example.client.data.repositories.GooglePlacesRepository
import javax.inject.Inject

class GetPlaceDetailsUseCase @Inject constructor(private val googlePlacesRepository: GooglePlacesRepository) {
    suspend operator fun invoke(placeId: String) = googlePlacesRepository.getPlacesDetails(placeId)
}