package com.example.client.ui.normalNoteScreen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.repositories.ImagesRepository
import com.example.client.domain.usecases.note.GetNoteUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val getNoteUseCase: GetNoteUseCase,
    private val imagesRepository: ImagesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteDetailState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.GetNote -> getNote(event.id)
            is NoteDetailEvent.AvisoVisto -> avisoVisto()
            is NoteDetailEvent.LoadNoteImages -> loadNoteImages(event.id)
        }
    }

    private fun getNote(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getNoteUseCase(id)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            note = result.data,
                            isLoading = false
                        )
                    }
                    loadNoteImages(id)
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(aviso = UiEvent.ShowSnackbar(result.message), isLoading = false)
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

   private fun loadNoteImages(noteId: Int) {
        viewModelScope.launch {
            imagesRepository.loadNoteImages(noteId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.update {
                            it.copy(
                                note = it.note?.copy(photos = result.data),
                                isImagesLoading = false
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.update {
                            it.copy(aviso = UiEvent.ShowSnackbar(result.message), isImagesLoading = false)
                        }
                    }
                    is NetworkResult.Loading -> {
                        _uiState.update { it.copy(isImagesLoading = true) }
                    }
                }
            }
        }
    }

    private fun avisoVisto() {
        _uiState.update { it.copy(aviso = null) }
    }
}
