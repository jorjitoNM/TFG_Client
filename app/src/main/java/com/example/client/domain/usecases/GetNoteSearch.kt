package com.example.client.domain.usecases

import com.example.client.common.NetworkResult
import com.example.client.data.model.NoteDTO
import com.example.client.data.repositories.NoteRepository
import javax.inject.Inject

class GetNoteSearch @Inject constructor(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(title: String): NetworkResult<List<NoteDTO>> {
        return when (val result = noteRepository.getNotes()) {
            is NetworkResult.Error -> NetworkResult.Error(result.message)
            is NetworkResult.Success -> {
                val noteSearched = result.data.filter { note ->
                    note.title.startsWith(title, ignoreCase = true)
                }
                NetworkResult.Success(noteSearched)
            }
            is NetworkResult.Loading -> NetworkResult.Loading()
        }
    }

}