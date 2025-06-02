package com.example.client.ui.normalNoteScreen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.di.IoDispatcher
import com.example.client.domain.model.note.NoteType
import com.example.client.domain.useCases.note.GetNoteSearchUseCase
import com.example.client.domain.useCases.note.GetNotesUseCase
import com.example.client.domain.useCases.note.OrderNoteByTypUseCase
import com.example.client.domain.useCases.note.OrderNoteUseCase
import com.example.client.domain.usecases.social.DelFavNoteUseCase
import com.example.client.domain.usecases.social.DelLikeNoteUseCase
import com.example.client.domain.usecases.social.FavNoteUseCase
import com.example.client.domain.usecases.social.LikeNoteUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
    private val getNoteSearchUseCase: GetNoteSearchUseCase,
    private val likeNoteUseCase: LikeNoteUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val delLikeNoteUseCase: DelLikeNoteUseCase,
    private val delFavNoteUseCase: DelFavNoteUseCase,
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
            is NoteListEvent.LikeNote -> likeNote(event.noteId)
            is NoteListEvent.GetNoteSearch -> searchNote(event.title)
            is NoteListEvent.DelFavNote -> delSavedNote(event.noteId)
            is NoteListEvent.DelLikeNote -> delLikedNote(event.noteId)
        }
    }

    private fun delSavedNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = delFavNoteUseCase.invoke(noteId)) {
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
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) {
                            note.copy(saved = false)
                        } else {
                            note
                        }
                    }
                    it.copy(
                        isLoading = false,
                        notes = updatedNotes
                    )
                }
            }
        }
    }

    private fun delLikedNote(noteId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = delLikeNoteUseCase.invoke(noteId)) {
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

                is NetworkResult.Success -> {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) {
                            note.copy(liked = false)
                        } else {
                            note
                        }
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            notes = updatedNotes
                        )
                    }
                }
            }
        }
    }

    private fun likeNote(noteId: Int) {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = likeNoteUseCase.invoke(noteId)) {
                is NetworkResult.Success -> {
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) {
                            note.copy(liked = true)
                        } else {
                            note
                        }
                    }
                    _uiState.update { it.copy(isLoading = false, notes = updatedNotes) }
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

    private fun orderByType(type: NoteType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = orderNoteByTypUseCase.invoke(type)) {
                is NetworkResult.Success -> {
                    if (result.data.isEmpty()) {
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

    private fun searchNote(title: Any) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getNoteSearchUseCase.invoke(title.toString())) {
                is NetworkResult.Error -> _uiState.update {
                    it.copy(
                        aviso = UiEvent.ShowSnackbar(result.message),
                        isLoading = false
                    )

                }
                is NetworkResult.Success -> {
                    val notes = result.data.toList()
                    _uiState.update { it.copy(notes = notes) }

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
            when (val result = favNoteUseCase.invoke(noteId)) {
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
                    val updatedNotes = uiState.value.notes.map { note ->
                        if (note.id == noteId) {
                            note.copy(saved = true)
                        } else {
                            note
                        }
                    }
                    it.copy(
                        isLoading = false,
                        notes = updatedNotes
                    )
                }
            }
        }
    }
}