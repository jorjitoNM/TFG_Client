package com.example.client.data.model

import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType


class EventNoteDTO(
    baseNote: NoteDTO = NoteDTO(),
    var start: String = "",
    var end: String = ""
) : NoteDTO(
    id = baseNote.id,
    title = baseNote.title,
    content = baseNote.content,
    privacy = baseNote.privacy,
    rating = baseNote.rating,
    ownerUsername = baseNote.ownerUsername,
    likes = baseNote.likes,
    created = baseNote.created,
    latitude = baseNote.latitude,
    longitude = baseNote.longitude,
    type = NoteType.EVENT
)