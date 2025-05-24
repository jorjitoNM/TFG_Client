package com.example.client.ui.addNoteScreen

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.di.IoDispatcher
import com.example.client.domain.usecases.AddNota
import com.example.client.ui.common.UiEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
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
        }
    }

    private fun addNote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentState = _uiState.value
            val currentNote = currentState.note

            when (val result = addNota(currentNote, "user1")) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        uiEvent = UiEvent.ShowSnackbar(result.message),
                        isLoading = false
                    )
                }

                is NetworkResult.Loading -> _uiState.update {
                    it.copy(
                        isLoading = true
                    )
                }

                is NetworkResult.Success -> _uiState.update {
                    it.copy(
                        note = result.data,
                        isLoading = false,
                        uiEvent = UiEvent.ShowSnackbar("Nota añadida correctamente")
                    )
                }

                null -> TODO()
            }
        }
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
                    location?.let {
                        _uiState.update { state ->
                            state.copy(currentLocation = location)
                        }
                    }
                }
            }
        }
    }

}