package com.example.client.ui.noteMap.search

import androidx.lifecycle.ViewModel
import com.example.client.domain.model.note.NoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedLocationViewModel @Inject constructor() : ViewModel() {
    private val _selectedLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val selectedLocation: StateFlow<Pair<Double, Double>?> = _selectedLocation.asStateFlow()

    private val _selectedNoteType = MutableStateFlow<NoteType?>(null)
    val selectedNoteType: StateFlow<NoteType?> = _selectedNoteType.asStateFlow()

    fun setLocation(lat: Double, lon: Double) {
        _selectedLocation.value = lat to lon
    }

    fun setNoteType(noteType: NoteType?) {
        _selectedNoteType.value = noteType
    }
}
