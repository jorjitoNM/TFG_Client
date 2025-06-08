package com.example.client.ui.addNoteScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.repositories.ImagesRepository
import com.example.client.domain.usecases.AddNota
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val addNota: AddNota,
    private val imagesRepository: ImagesRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<AddNoteState> = MutableStateFlow(AddNoteState())
    val uiState: StateFlow<AddNoteState> = _uiState

    fun handleEvent(event: AddNoteEvents) {
        when (event) {
            is AddNoteEvents.AddNoteNote -> addNoteAndUploadImages()
            is AddNoteEvents.EditNote -> {
                _uiState.update { it.copy(note = event.note) }
            }
            is AddNoteEvents.AddNoteImages -> {
                _uiState.update { it.copy(selectedImages = event.uris) }
            }
            is AddNoteEvents.UiNoteEventsDone -> _uiState.update { it.copy(uiEvent = null) }
        }
    }

    private fun addNoteAndUploadImages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val noteToSave = _uiState.value.note
            val imagesToUpload = _uiState.value.selectedImages

            when (val result = addNota(noteToSave)) {
                is NetworkResult.Success -> {
                    val createdNote = result.data
                    if (imagesToUpload.isNotEmpty()) {
                        imagesRepository.saveNoteImages(imagesToUpload, createdNote.id).collect { imgResult ->
                            when (imgResult) {
                                is NetworkResult.Success -> {
                                    _uiState.update {
                                        it.copy(
                                            note = createdNote.copy(photos = imgResult.data),
                                            isLoading = false,
                                            uiEvent = UiEvent.PopBackStack,
                                            isNoteCreated = true
                                        )
                                    }
                                }
                                is NetworkResult.Error -> _uiState.update {
                                    it.copy(uiEvent = UiEvent.ShowSnackbar(imgResult.message), isLoading = false)
                                }
                                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                note = createdNote,
                                isLoading = false,
                                uiEvent = UiEvent.ShowSnackbar("Note created!"),
                                isNoteCreated = true
                            )
                        }
                    }
                }
                is NetworkResult.Error -> _uiState.update {
                    it.copy(uiEvent = UiEvent.ShowSnackbar(result.message), isLoading = false)
                }
                is NetworkResult.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }



}
