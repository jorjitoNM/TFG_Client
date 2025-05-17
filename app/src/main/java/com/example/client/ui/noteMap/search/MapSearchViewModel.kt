package com.example.client.ui.noteMap.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.domain.model.note.NominatimPlace
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
class MapSearchViewModel @Inject constructor()
 : ViewModel() {
    private val _uiState = MutableStateFlow(MapSearchState())
    val uiState = _uiState.asStateFlow()
    private var searchJob: Job? = null
    fun handleEvent(event: MapSearchEvent) {
        when (event) {
            is MapSearchEvent.UpdateSearchText -> {
                _uiState.update { it.copy(searchText = event.query, isLoading = true) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500) // Espera 500ms después de la última tecla
                    if (event.query.isNotBlank()) {
                        val results = withContext(Dispatchers.IO) {
                            searchPlaces(event.query)
                        }
                        _uiState.update {
                            it.copy(
                                results = results,
                                isLoading = false,
                                showEmptyState = results.isEmpty()
                            )
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
            is MapSearchEvent.NavigateBack -> _uiState.update { it.copy(aviso = UiEvent.PopBackStack) }
            is MapSearchEvent.AvisoVisto -> _uiState.update { it.copy(aviso = null) }
            // ...
        }
    }

}


suspend fun searchPlaces(query: String): List<NominatimPlace> {
    val client = OkHttpClient()
    val url = "https://nominatim.openstreetmap.org/search?q=${query}&format=json&addressdetails=1"
    val request = Request.Builder()
        .url(url)
        .header("User-Agent", "TFG_Client/1.0 (saavedra.mateo.walter@gmail.com)")
        .build()
    val response = client.newCall(request).execute()
    val body = response.body?.string() ?: return emptyList()
    return Gson().fromJson(body, Array<NominatimPlace>::class.java).toList()
}