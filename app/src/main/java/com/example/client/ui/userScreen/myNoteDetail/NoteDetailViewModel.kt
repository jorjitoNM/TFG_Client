package com.example.client.ui.userScreen.myNoteDetail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.data.repositories.ImagesRepository
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.usecases.note.DeleteNoteUseCase
import com.example.client.domain.usecases.note.GetNoteUseCase
import com.example.client.domain.usecases.note.RateNoteUseCase
import com.example.client.domain.usecases.note.UpdateNoteUseCase
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
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val rateNoteUseCase: RateNoteUseCase,
    private val imagesRepository: ImagesRepository,
    private val deleteNoteUseCase: DeleteNoteUseCase // <-- Añadir aquí

) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteDetailState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.GetNote -> getNote(event.id)
            is NoteDetailEvent.UpdateNote -> updateNote()
            is NoteDetailEvent.RateNote -> rateNote(event.rating)
            is NoteDetailEvent.ToggleEditMode -> toggleEditMode()
            is NoteDetailEvent.UpdateEditedTitle -> updateEditedTitle(event.title)
            is NoteDetailEvent.UpdateEditedContent -> updateEditedContent(event.content)
            is NoteDetailEvent.UpdateEditedPrivacy -> updateEditedPrivacy(event.privacy)
            is NoteDetailEvent.AvisoVisto -> avisoVisto()
            is NoteDetailEvent.SaveNoteImages -> saveNoteImages(event.imagesUris)
            is NoteDetailEvent.DeleteImage -> deleteImage(event.imageUri)
            is NoteDetailEvent.DeleteNote -> deleteNote()
        }
    }

    private fun deleteNote() {
        val noteId = _uiState.value.note?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = deleteNoteUseCase(noteId)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            aviso = UiEvent.PopBackStack,
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
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
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
                            isLoading = false,
                            editedTitle = result.data.title,
                            editedContent = result.data.content ?: "",
                            editedPrivacy = result.data.privacy
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

    fun loadNoteImages(noteId: Int) {
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

    private fun saveNoteImages(imagesUris: List<Uri>) {
        val noteId = _uiState.value.note?.id ?: return
        viewModelScope.launch {
            imagesRepository.saveNoteImages(imagesUris, noteId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        loadNoteImages(noteId)
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


    private fun deleteImage(imageUri: Uri) {
        val noteId = _uiState.value.note?.id ?: return
        viewModelScope.launch {
            imagesRepository.deleteImage(imageUri, noteId).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        loadNoteImages(noteId)
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

    private fun updateNote() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentNote = currentState.note ?: return@launch
            val updatedNote = currentNote.copy(
                title = currentState.editedTitle,
                content = currentState.editedContent,
                privacy = currentState.editedPrivacy
            )
            when (val result = updateNoteUseCase(updatedNote)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            note = result.data,
                            isLoading = false,
                            isEditing = false,
                            aviso = UiEvent.ShowSnackbar("Note updated successfully")
                        )
                    }
                    loadNoteImages(result.data.id)
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


    private fun rateNote(rating: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val currentNote = _uiState.value.note ?: return@launch
            when (val result = rateNoteUseCase(currentNote.id, rating)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            note = result.data.copy(photos = currentNote.photos), // Mantén las fotos actuales
                            isUpdating = false,
                            aviso = UiEvent.ShowSnackbar("Rating updated successfully")
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(aviso = UiEvent.ShowSnackbar(result.message), isUpdating = false)
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.update { it.copy(isUpdating = true) }
                }
            }
        }
    }



    private fun toggleEditMode() {
        val currentState = _uiState.value
        if (currentState.isEditing) {
            val currentNote = currentState.note
            if (currentNote != null) {
                _uiState.update {
                    it.copy(
                        isEditing = false,
                        editedTitle = currentNote.title,
                        editedContent = currentNote.content ?: "",
                        editedPrivacy = currentNote.privacy
                    )
                }
            } else {
                _uiState.update { it.copy(isEditing = false) }
            }
        } else {
            _uiState.update { it.copy(isEditing = true) }
        }
    }

    private fun updateEditedTitle(title: String) {
        _uiState.update { it.copy(editedTitle = title) }
    }

    private fun updateEditedContent(content: String) {
        _uiState.update { it.copy(editedContent = content) }
    }

    private fun updateEditedPrivacy(privacy: NotePrivacy) {
        _uiState.update { it.copy(editedPrivacy = privacy) }
    }

    private fun avisoVisto() {
        _uiState.update { it.copy(aviso = null) }
    }
}
