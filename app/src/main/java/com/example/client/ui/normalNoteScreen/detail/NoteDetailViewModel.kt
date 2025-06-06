package com.example.client.ui.normalNoteScreen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.model.note.NotePrivacy
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
    private val rateNoteUseCase: RateNoteUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteDetailState())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.GetNote -> getNote(event.id)
            is NoteDetailEvent.AvisoVisto -> avisoVisto()
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
                            // Initialize edited fields with the note data
                            editedTitle = result.data.title,
                            editedContent = result.data.content ?: "",
                            editedPrivacy = result.data.privacy
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

    private fun avisoVisto() {
        _uiState.update { it.copy(aviso = null) }
    }
}