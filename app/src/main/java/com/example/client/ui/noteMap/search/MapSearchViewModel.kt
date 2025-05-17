package com.example.client.ui.noteMap.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.model.note.NominatimPlace
import com.example.client.domain.usecases.map.SearchPlacesUseCase
import com.example.client.ui.common.UiEvent
import com.example.client.ui.noteMap.list.NoteMapState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

@HiltViewModel
class MapSearchViewModel @Inject constructor(
    private val searchPlacesUseCase: SearchPlacesUseCase
)
 : ViewModel() {
    private val _uiState = MutableStateFlow(MapSearchState())
    val uiState = _uiState.asStateFlow()
    private var searchJob: Job? = null
    fun handleEvent(event: MapSearchEvent) {
        when (event) {
            is MapSearchEvent.UpdateSearchText -> searchPlaces(event.query)
            is MapSearchEvent.NavigateBack -> _uiState.update { it.copy(aviso = UiEvent.PopBackStack) }
            is MapSearchEvent.AvisoVisto -> _uiState.update { it.copy(aviso = null) }
            // ...
        }
    }

    private fun searchPlaces (query: String) {
        _uiState.update { it.copy(searchText = query, isLoading = true) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (query.isNotBlank()) {
                when (val result = searchPlacesUseCase(query)) {
                    is NetworkResult.Success -> _uiState.update {
                        it.copy(
                            results = result.data,
                            isLoading = false,
                            showEmptyState = result.data.isEmpty()
                        )
                    }
                    is NetworkResult.Error -> _uiState.update {
                        it.copy(
                            results = emptyList(),
                            isLoading = false,
                            showEmptyState = false,
                            aviso = UiEvent.ShowSnackbar(result.message)
                        )
                    }
                    is NetworkResult.Loading -> _uiState.update {
                        it.copy(isLoading = true)
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        results = emptyList(),
                        isLoading = false,
                        showEmptyState = false,
                    )
                }
            }
        }
    }

}


