package com.example.client.ui.noteMap.list

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.model.note.NoteType
import com.example.client.domain.usecases.note.GetGroupedNotesByZoomUseCase
import com.example.client.domain.usecases.note.GetNoteSearch
import com.example.client.domain.usecases.note.GetNotesUseCase
import com.example.client.domain.usecases.note.OrderNoteByTypUseCase
import com.example.client.ui.common.UiEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteMapViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val getGroupedNotesByZoomUseCase: GetGroupedNotesByZoomUseCase,
    private val getNoteSearch: GetNoteSearch,
    private val orderNoteByTypUseCase: OrderNoteByTypUseCase,
    private val application: Application
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteMapState())
    val uiState = _uiState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }

    fun handleEvent(event: NoteMapEvent) {
        when (event) {
            is NoteMapEvent.GetNotes -> getNotes()
            is NoteMapEvent.AvisoVisto -> avisoVisto()
            is NoteMapEvent.GetCurrentLocation -> getCurrentLocation()
            is NoteMapEvent.CheckLocationPermission -> checkLocationPermission()
            is NoteMapEvent.SearchNote -> searchNote(event.query)
            is NoteMapEvent.SaveCameraPosition -> saveCameraPosition(event.latLng, event.zoom)
            is NoteMapEvent.UpdateSelectedType -> updateSelectedType(event.noteType)
            is NoteMapEvent.UpdateSearchText -> updateSearchText(event.text)
            is NoteMapEvent.FilterByType -> filterByType(event.noteType)
            is NoteMapEvent.GetGroupedNotesByZoom -> getGroupedNotesByZoom(event.zoom)

        }
    }

    private fun filterByType(noteType: NoteType?) {
        if (noteType == null) {
            // Si no hay tipo seleccionado, carga todas las notas
            getNotes()
            _uiState.update { it.copy(selectedType = null) }
        } else {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, selectedType = noteType) }
                when (val result = orderNoteByTypUseCase(noteType)) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(notes = result.data, isLoading = false)
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(
                                aviso = UiEvent.ShowSnackbar(result.message ?: "Unknown error"),
                                isLoading = false
                            )
                        }
                    }
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun getGroupedNotesByZoom(zoom: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getGroupedNotesByZoomUseCase(zoom)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(groupedNotes = result.data, isLoading = false)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(aviso = UiEvent.ShowSnackbar(result.message ?: "Unknown error"), isLoading = false)
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }



    private fun updateSearchText(text: String) {
        _uiState.update { it.copy(currentSearch = text) }
    }

    private fun updateSelectedType(noteType: NoteType?) {
        _uiState.update { it.copy(selectedType = noteType) }
    }

    private fun saveCameraPosition(latLng: com.google.android.gms.maps.model.LatLng, zoom: Float) {
        _uiState.update { it.copy(cameraLatLng = latLng, cameraZoom = zoom) }
    }

    private fun searchNote(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getNoteSearch(query)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(notes = result.data, isLoading = false)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message ?: "Unknown error"),
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun getNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getNotesUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(notes = result.data, isLoading = false)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message ?: "Unknown error"),
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun checkLocationPermission() {
        val hasPermission =
            ActivityCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            if (
                ActivityCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(application, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        _uiState.update { state ->
                            state.copy(currentLocation = location)
                        }
                    }
                }
            }
        }
    }

    private fun avisoVisto() {
        _uiState.update { it.copy(aviso = null) }
    }
}