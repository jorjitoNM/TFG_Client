package com.example.client.ui.noteMap.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.BuildConfig
import com.example.client.R
import com.example.client.common.NetworkResult
import com.example.client.common.StringProvider
import com.example.client.domain.model.google.Location
import com.example.client.domain.model.google.PlacePhoto
import com.example.client.domain.model.google.Prediction
import com.example.client.domain.usecases.map.GetPlaceAutoCompleteUseCase
import com.example.client.domain.usecases.map.GetPlaceDetailsUseCase
import com.example.client.domain.usecases.map.local.DeleteCachedLocationUseCase
import com.example.client.domain.usecases.map.local.GetCachedLocationsUseCase
import com.example.client.domain.usecases.map.local.InsertCachedLocationUseCase
import com.example.client.domain.usecases.user.GetUserUseCase
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.getGooglePhotoUrl
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
    private val getCachedLocationsUseCase: GetCachedLocationsUseCase,
    private val insertCachedLocationUseCase: InsertCachedLocationUseCase,
    private val deleteCachedLocationUseCase: DeleteCachedLocationUseCase,
    private val stringProvider: StringProvider,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapSearchState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    private fun getLoggedUser() {
        viewModelScope.launch {
            when (val user = getUserUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(userLogged = user.data.username) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(user.message)) }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    init {
        getLoggedUser()
    }

    fun handleEvent(event: MapSearchEvent) {
        when (event) {
            is MapSearchEvent.UpdateSearchText -> onSearchTextChanged(event.text)
            is MapSearchEvent.NavigateBack -> _uiState.update { it.copy(aviso = UiEvent.PopBackStack) }
            is MapSearchEvent.AvisoVisto -> _uiState.update { it.copy(aviso = null) }
            is MapSearchEvent.ShowSnackbar -> showSnackbar(event.message)
            is MapSearchEvent.LoadRecents -> loadRecents(event.userLogged)
            is MapSearchEvent.InsertRecent -> insertRecent(event.location, event.userLogged)
            is MapSearchEvent.DeleteRecent -> deleteRecent(event.id, event.userLogged)
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
                when (val autoResult = getPlaceAutoCompleteUseCase(text)) {
                    is NetworkResult.Success -> {
                        val predictions = autoResult.data.predictions.take(7)
                        val results = predictions.mapNotNull { prediction ->
                            getLocationForPrediction(prediction)
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
            val userLogged = _uiState.value.userLogged
            if (userLogged != null) {
                handleEvent(MapSearchEvent.LoadRecents(userLogged))
            }
            _uiState.update {
                it.copy(
                    results = emptyList(),
                    isLoading = false,
                    showEmptyState = false
                )
            }
        }
    }

    private suspend fun getLocationForPrediction(prediction: Prediction): Location? {
        return when (val detailsResult = getPlaceDetailsUseCase(prediction.placeId)) {
            is NetworkResult.Success -> {
                val result = detailsResult.data.result
                val photoUrl = result.photos?.firstOrNull()?.photoReference?.let {
                    getGooglePhotoUrl(it)
                }
                val openingHoursText = result.openingHours?.let { oh ->
                    when (oh.openNow) {
                        true -> "Open now"
                        false -> "Closed"
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
                    } ?: emptyList(),
                    openingHoursFull = result.openingHours?.weekdayText
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


    private fun loadRecents(userLogged: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showEmptyStateDelayed = false) }
            when (val result = getCachedLocationsUseCase(userLogged)) {
                is NetworkResult.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                recents = result.data,
                                isLoading = false,
                                showEmptyState = false,
                                showEmptyStateDelayed = true
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                recents = result.data,
                                isLoading = false,
                                showEmptyState = false,
                                showEmptyStateDelayed = false
                            )
                        }
                    }
                }
                is NetworkResult.Error -> _uiState.update { it.copy(isLoading = false, aviso = UiEvent.ShowSnackbar(result.message), showEmptyStateDelayed = false) }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true, showEmptyStateDelayed = false) }
            }
        }
    }


    private fun insertRecent(location: Location, userLogged: String) {
        viewModelScope.launch {
            when (val result = insertCachedLocationUseCase(location, userLogged)) {
                is NetworkResult.Success -> loadRecents(userLogged)
                is NetworkResult.Error -> _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message)) }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }


    private fun deleteRecent(id: Int, userLogged: String) {
        viewModelScope.launch {
            when (val result = deleteCachedLocationUseCase(id, userLogged)) {
                is NetworkResult.Success -> loadRecents(userLogged)
                is NetworkResult.Error -> _uiState.update { it.copy(aviso = UiEvent.ShowSnackbar(result.message)) }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }


}
