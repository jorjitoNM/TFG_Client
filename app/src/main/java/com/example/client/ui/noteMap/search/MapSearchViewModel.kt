    package com.example.client.ui.noteMap.search

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.client.R
    import com.example.client.common.NetworkResult
    import com.example.client.common.StringProvider
    import com.example.client.domain.model.google.*
    import com.example.client.domain.usecases.map.*
    import com.example.client.ui.common.UiEvent
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.Job
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch
    import javax.inject.Inject

    @HiltViewModel
    class MapSearchViewModel @Inject constructor(
        private val getPlaceAutoCompleteUseCase: GetPlaceAutoCompleteUseCase,
        private val getPlaceDetailsUseCase: GetPlaceDetailsUseCase,
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
                is MapSearchEvent.ShowSnackbar -> showSnackbar(event.message)
            }
        }

        private fun showSnackbar(message: String) {
            _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(message)) }
        }

        private fun onSearchTextChanged(text: String) {
            _uiState.update { it.copy(searchText = text, isLoading = true) }
            searchJob?.cancel()
            if (text.isNotBlank()) {
                searchJob = viewModelScope.launch {
                    delay(1500)
                    val apiKey = stringProvider.getString(R.string.google_maps_key)
                    when (val autoResult = getPlaceAutoCompleteUseCase(text, apiKey)) {
                        is NetworkResult.Success -> {
                            val predictions = autoResult.data.predictions.take(5)
                            val results = predictions.mapNotNull { prediction ->
                                getLocationForPrediction(prediction, apiKey)
                            }
                            _uiState.update {
                                it.copy(
                                    results = results,
                                    isLoading = false,
                                    showEmptyState = results.isEmpty()
                                )
                            }
                        }
                        is NetworkResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    showEmptyState = true,
                                    aviso = UiEvent.ShowSnackbar(autoResult.message)
                                )
                            }
                        }
                        is NetworkResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
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
        private suspend fun getLocationForPrediction(prediction: Prediction, apiKey: String): Location? {
            return when (val detailsResult = getPlaceDetailsUseCase(prediction.placeId, apiKey)) {
                is NetworkResult.Success -> {
                    val result = detailsResult.data.result
                    val photoUrl = result.photos?.firstOrNull()?.photoReference?.let {
                        getGooglePhotoUrl(it, apiKey)
                    }
                    val openingHoursText = result.openingHours?.let { oh ->
                        when (oh.openNow) {
                            true -> "Abierto ahora"
                            false -> "Cerrado ahora"
                            else -> null
                        }
                    }
                    Location(
                        name = result.name ?: "",
                        address = result.formattedAddress ?: "",
                        lat = result.geometry?.location?.lat ?: 0.0,
                        lng = result.geometry?.location?.lng ?: 0.0,
                        photoUrl = photoUrl,
                        rating = result.rating,
                        userRatingsTotal = result.userRatingsTotal,
                        openingHours = openingHoursText,
                        phoneNumber = result.formattedPhoneNumber,
                        website = result.website,
                        photos = result.photos?.map { photo ->
                            PlacePhoto(photoReference = photo.photoReference)
                        } ?: emptyList()
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showEmptyState = true,
                            aviso = UiEvent.ShowSnackbar(detailsResult.message)
                        )
                    }
                    null
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                    null
                }


            }
        }

    }

     fun getGooglePhotoUrl(photoReference: String, apiKey: String, maxWidth: Int = 400): String =
        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxWidth&photoreference=$photoReference&key=$apiKey"




