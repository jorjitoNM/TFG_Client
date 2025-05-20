package com.example.client.domain.usecases.map

import com.example.client.data.repositories.NominatimRepository
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(private val nominatimRepository: NominatimRepository) {
    suspend operator fun invoke(query: String) = nominatimRepository.searchPlaces(query)
}