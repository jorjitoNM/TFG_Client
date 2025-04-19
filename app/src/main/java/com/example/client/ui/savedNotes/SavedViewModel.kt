package com.example.client.ui.savedNotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.common.NetworkResult
import com.example.client.domain.usecases.note.OrderNoteUseCase
import com.example.client.domain.usecases.social.GetNoteSavedUseCase
import com.example.client.ui.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val getNoteSavedUseCase: GetNoteSavedUseCase,
    private val orderNoteUseCase: OrderNoteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedSate())
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: SavedEvent) {
        when (event) {
            SavedEvent.GetNotes -> getNotes()
            SavedEvent.AvisoVisto -> avisoVisto()
            is SavedEvent.ApplyFilter -> filter(event.asc)
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

    private fun avisoVisto() {
        _uiState.update { it.copy(aviso = null) }
    }

    private fun getNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = getNoteSavedUseCase()) {
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

}