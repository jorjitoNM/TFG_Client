package com.example.client.domain.useCases

import android.provider.ContactsContract.CommonDataKinds.Note
import javax.inject.Inject

class AddNota @Inject constructor(private val notesRepository: NotesRepository) {
    operator fun invoke(note : Note) = notesRepository.addNotes(note)
}