package com.example.client.ui.noteMap.list

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import com.google.android.gms.maps.model.LatLng
sealed class NoteMapEvent {
    data object AvisoVisto : NoteMapEvent()
    data object GetNotes : NoteMapEvent()
    data object GetCurrentLocation : NoteMapEvent()
    data object CheckLocationPermission : NoteMapEvent()
    data class SearchNote(val query: String) : NoteMapEvent()
    data class SaveCameraPosition(val latLng: LatLng, val zoom: Float) : NoteMapEvent()
    data class UpdateSelectedType(val noteType: NoteType?) : NoteMapEvent()
    data class UpdateSearchText(val text: String) : NoteMapEvent()
    data class FilterByType(val noteType: NoteType?) : NoteMapEvent()
    data object NavigateToSearch : NoteMapEvent()
    data class SelectedNote (val noteId: Int) : NoteMapEvent()
    data class GetSelectedNotesImages(val selectedNotes: SnapshotStateList<NoteDTO>) : NoteMapEvent()
}