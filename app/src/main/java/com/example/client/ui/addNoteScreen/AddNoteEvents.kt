package com.example.client.ui.addNoteScreen

import android.net.Uri
import com.example.client.data.model.NoteDTO


sealed interface AddNoteEvents {
    data object AddNoteNote : AddNoteEvents
    data object UiNoteEventsDone : AddNoteEvents
    data class EditNote(val note: NoteDTO) : AddNoteEvents
    data class AddNoteImages(val uris: List<Uri>) : AddNoteEvents
}