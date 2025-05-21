package com.example.client.ui.noteMap.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.R
import com.example.client.common.StringProvider
import com.example.client.data.remote.service.GooglePlacesService
import com.example.client.domain.model.GooglePlaceUi
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSearchViewModel @Inject constructor(
    private val googlePlacesService: GooglePlacesService,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapSearchState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun handleEvent(event: MapSearchEvent) {
        when (event) {
            is MapSearchEvent.UpdateSearchText -> onSearchTextChanged(event.text)
            is MapSearchEvent.NavigateBack -> {
                _uiState.update { it.copy(aviso = UiEvent.PopBackStack) }
            }
            is MapSearchEvent.AvisoVisto -> {
                _uiState.update { it.copy(aviso = null) }
            }
        }
    }

    private fun onSearchTextChanged(text: String) {
        _uiState.update { it.copy(searchText = text, isLoading = true) }
        searchJob?.cancel()
        if (text.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(1500)
                try {
                    val response = googlePlacesService.getAutocomplete(
                        text,
                        stringProvider.getString(R.string.google_maps_key)
                    )
                    val predictions = response.predictions

                    val results = predictions.take(5).map { prediction ->
                        async {
                                val details = googlePlacesService.getPlaceDetails(
                                    placeId = prediction.place_id,
                                    apiKey = stringProvider.getString(R.string.google_maps_key)
                                )
                                val photoUrl = details.result.photos?.firstOrNull()?.photo_reference?.let {
                                    getGooglePhotoUrl(it, stringProvider.getString(R.string.google_maps_key))
                                }
                                GooglePlaceUi(
                                    name = details.result.name ?: "",
                                    address = details.result.formatted_address ?: "",
                                    lat = details.result.geometry?.location?.lat ?: 0.0,
                                    lng = details.result.geometry?.location?.lng ?: 0.0,
                                    photoUrl = photoUrl
                                )
                        }
                    }.awaitAll().filterNotNull()

                    _uiState.update {
                        it.copy(
                            results = results,
                            isLoading = false,
                            showEmptyState = results.isEmpty()
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showEmptyState = true,
                            aviso = UiEvent.ShowSnackbar("Error al buscar ubicaciones")
                        )
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    results = emptyList(),
                    isLoading = false,
                    showEmptyState = false
                )
            }
        }
    }
}

fun getGooglePhotoUrl(photoReference: String, apiKey: String, maxWidth: Int = 400): String =
    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&photoreference=$photoReference&key=$apiKey"



