package com.example.client.ui.noteScreen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.usecases.GetNotesUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteListState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.SelectedNote -> selectNote(event.noteId)
            is NoteListEvent.GetNotes -> getNotes()
            is NoteListEvent.AvisoVisto -> avisoVisto()
        }
    }

    private fun getNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getNotesUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            notes = result.data,
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.ShowSnackbar(result.message),
                            isLoading = false
                        )
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun selectNote(id: Int) {
        _uiState.update {
            it.copy(
                selectedNoteId = id,
                aviso = UiEvent.PopBackStack
            )
        }
    }

    private fun avisoVisto() {
        _uiState.update { it.copy(aviso = null) }
    }
}