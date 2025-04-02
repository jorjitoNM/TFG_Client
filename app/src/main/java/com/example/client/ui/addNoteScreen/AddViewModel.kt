package com.example.client.ui.addNoteScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.domain.model.note.Note
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
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState: MutableStateFlow<AddState> = MutableStateFlow(AddState())
    val uiState: StateFlow<AddState> = _uiState

    fun handleEvent(event: AddEvent) {
        when (event) {
            is AddEvent.addNote -> addNote()
            is AddEvent.UiEventDone -> _uiState.update { it.copy(uiEvent = null) }
        }
    }

    private fun addNote() {
        viewModelScope.launch(dispatcher) {

        }
    }

}