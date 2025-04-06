package com.example.client.domain.usecases

import com.example.client.data.repositories.NoteRepository
import com.example.client.ui.noteScreen.list.NoteDTO
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(note:NoteDTO, username: String) = noteRepository.updateNote(note, username)
}