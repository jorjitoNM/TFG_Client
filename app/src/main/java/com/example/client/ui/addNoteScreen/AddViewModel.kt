package com.example.client.ui.addNoteScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.model.note.Note
import com.example.client.domain.useCases.AddNota
import com.example.client.ui.common.UiEvent
import com.example.musicapprest.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val addNota: AddNota,
) : ViewModel() {

    private val _uiState: MutableStateFlow<AddState> = MutableStateFlow(AddState())
    val uiState: StateFlow<AddState> = _uiState

    fun handleEvent(event: AddEvent) {
        when (event) {
            is AddEvent.addNote -> addNote()
            is AddEvent.UiEventDone -> _uiState.update { it.copy(uiEvent = null) }
            is AddEvent.editNote -> {
                _uiState.update { it.copy(note = event.note) }
            }
        }
    }

    private fun addNote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentState = _uiState.value
            val currentNote = currentState.note

            when (val result = currentNote?.let { addNota(it, "user1") }) {
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

}