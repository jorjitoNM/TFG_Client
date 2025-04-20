package com.example.client.ui.noteScreen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.model.note.NoteType
import com.example.client.domain.usecases.FavNoteUseCase
import com.example.client.domain.usecases.GetNotesUseCase
import com.example.client.domain.usecases.OrderNoteByTypUseCase
import com.example.client.domain.usecases.OrderNoteUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val favNoteUseCase: FavNoteUseCase,
    private val orderNoteUseCase: OrderNoteUseCase,
    private val orderNoteByTypUseCase: OrderNoteByTypUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteListState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.SelectedNote -> selectNote(event.noteId)
            is NoteListEvent.GetNotes -> getNotes()
            is NoteListEvent.AvisoVisto -> avisoVisto()
            is NoteListEvent.FavNote -> favNote(event.noteId)
            is NoteListEvent.ApplyFilter -> filter(event.asc)
            is NoteListEvent.OrderByType -> orderByType(event.type)
        }
    }

    private fun orderByType(type: NoteType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = orderNoteByTypUseCase.invoke(type)) {
                is NetworkResult.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        _uiState.update {
                            it.copy(
                                aviso = UiEvent.ShowSnackbar("No se encontraron notas para el tipo seleccionado."),
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                notes = result.data,
                                isLoading = false
                            )
                        }
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

    private fun filter(asc: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = (orderNoteUseCase.invoke(asc))) {
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

    private fun favNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = favNoteUseCase.invoke(noteId, "user1")) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        aviso = UiEvent.ShowSnackbar(result.message),
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
                        isLoading = false
                    )
                }
            }
        }
    }
}