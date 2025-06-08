package com.example.client.ui.normalNoteScreen.detail

import android.net.Uri
import com.example.client.domain.model.note.NotePrivacy

sealed class NoteDetailEvent {
    data class GetNote(val id: Int) : NoteDetailEvent()
    data object AvisoVisto : NoteDetailEvent()
    data class LoadNoteImages(val id: Int) : NoteDetailEvent()
}
