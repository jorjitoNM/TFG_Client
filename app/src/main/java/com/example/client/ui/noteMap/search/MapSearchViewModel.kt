package com.example.client.ui.noteMap.search

import androidx.lifecycle.ViewModel
import com.example.client.ui.common.UiEvent
import com.example.client.ui.noteMap.list.NoteMapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MapSearchViewModel @Inject constructor()
 : ViewModel() {
    private val _uiState = MutableStateFlow(MapSearchState())
    val uiState = _uiState.asStateFlow()
    fun handleEvent(event: MapSearchEvent) {
        when (event) {
            is MapSearchEvent.NavigateBack -> _uiState.update { it.copy(aviso = UiEvent.PopBackStack) }
            is MapSearchEvent.AvisoVisto -> _uiState.update { it.copy(aviso = null) }
            // ...
        }
    }

}