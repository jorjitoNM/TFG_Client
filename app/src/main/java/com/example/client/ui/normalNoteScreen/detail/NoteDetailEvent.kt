package com.example.client.ui.normalNoteScreen.detail

import com.example.client.domain.model.note.NotePrivacy

sealed interface NoteDetailEvent {
    data class GetNote(val id: Int) : NoteDetailEvent
    data object UpdateNote : NoteDetailEvent
    data class RateNote(val rating: Int) : NoteDetailEvent
    data object ToggleEditMode : NoteDetailEvent
    data class UpdateEditedTitle(val title: String) : NoteDetailEvent
    data class UpdateEditedContent(val content: String) : NoteDetailEvent
    data class UpdateEditedPrivacy(val privacy: NotePrivacy) : NoteDetailEvent
    data class LikeNote (val noteId : Int) : NoteDetailEvent
    data object AvisoVisto : NoteDetailEvent
}