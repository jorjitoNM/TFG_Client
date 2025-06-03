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
    private val addNota: AddNota
) : ViewModel() {

    private val _uiState: MutableStateFlow<AddNoteState> = MutableStateFlow(AddNoteState())
    val uiState: StateFlow<AddNoteState> = _uiState

    fun handleEvent(event: AddNoteEvents) {
        when (event) {
            is AddNoteEvents.AddNoteNote -> addNote()
            is AddNoteEvents.UiNoteEventsDone -> _uiState.update { it.copy(uiEvent = null) }
            is AddNoteEvents.EditNote -> {
                _uiState.update { it.copy(note = event.note) }
            }
        }
    }

    private fun addNote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentNote = _uiState.value.note

            when (val result = addNota(currentNote)) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        uiEvent = UiEvent.PopBackStack,
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
                        uiEvent = UiEvent.PopBackStack
                    )
                }
            }
        }
    }
}
