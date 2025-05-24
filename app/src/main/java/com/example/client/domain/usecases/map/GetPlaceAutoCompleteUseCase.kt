package com.example.client.domain.usecases.map

import com.example.client.data.repositories.GooglePlacesRepository
import javax.inject.Inject

class GetPlaceAutoCompleteUseCase @Inject constructor(private val repository: GooglePlacesRepository) {
    suspend operator fun invoke(input: String, apiKey: String) = repository.getPlacesAutocomplete(input, apiKey)
}