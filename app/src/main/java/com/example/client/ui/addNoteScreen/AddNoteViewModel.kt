package com.example.client.ui.addNoteScreen

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.model.NoteDTO
import com.example.client.domain.usecases.AddNota
import com.example.client.ui.common.UiEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val addNota: AddNota,
    private val application: Application
) : ViewModel() {

    private val _uiState: MutableStateFlow<AddNoteState> = MutableStateFlow(AddNoteState())
    val uiState: StateFlow<AddNoteState> = _uiState


    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }

    fun handleEvent(event: AddNoteEvents) {
        when (event) {
            is AddNoteEvents.AddNoteNote -> addNote()
            is AddNoteEvents.UiNoteEventsDone -> _uiState.update { it.copy(uiEvent = null) }
            is AddNoteEvents.EditNote -> {
                _uiState.update { it.copy(note = event.note) }
            }
            is AddNoteEvents.GetCurrentLocation -> getCurrentLocation()
            is AddNoteEvents.CheckLocationPermission -> checkLocationPermission()

        }
    }

    private fun addNote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentState = _uiState.value
            val currentNote = currentState.note
            currentNote.latitude = currentState.currentLocation?.latitude ?: 0.0
            currentNote.longitude = currentState.currentLocation?.longitude ?: 0.0

            when (val result = addNota(currentNote)) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        uiEvent = UiEvent.ShowSnackbar(result.message),
                        isLoading = false
                    )
                }

                is NetworkResult.Loading -> _uiState.update {
                    it.copy(isLoading = true)
                }

                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        note = NoteDTO(), 
                        isLoading = false,
                        uiEvent = UiEvent.ShowSnackbar("Nota añadida correctamente")
                    )
                }

                null -> TODO()
            }
        }
    }

    private fun checkLocationPermission() {
        val hasPermission = ActivityCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        _uiState.update { it.copy(hasLocationPermission = hasPermission) }
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    application,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        _uiState.update { state ->
                            state.copy(currentLocation = location)
                        }
                    } else {
                        _uiState.update { state ->
                            state.copy(
                                uiEvent = UiEvent.ShowSnackbar("No se pudo obtener la ubicación actual")
                            )
                        }
                    }
                }.addOnFailureListener {
                    _uiState.update { state ->
                        state.copy(
                            uiEvent = UiEvent.ShowSnackbar("Error al obtener la ubicación: ${it.message}")
                        )
                    }
                }
            } else {
                _uiState.update { state ->
                    state.copy(
                        uiEvent = UiEvent.ShowSnackbar("Permisos de ubicación no otorgados")
                    )
                }
            }
        }
    }

}