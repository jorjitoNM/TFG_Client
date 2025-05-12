package com.example.client.domain.usecases.note

import com.example.client.data.repositories.NoteRepository
import javax.inject.Inject

class GetGroupedNotesByZoomUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(zoom: Float) = noteRepository.getGroupNotesByZoom(zoom)
}
