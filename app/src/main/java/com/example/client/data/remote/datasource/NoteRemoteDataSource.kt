package com.example.client.data.remote.datasource

import com.example.client.data.model.NoteDTO
import com.example.client.data.remote.service.NoteService
import com.example.client.domain.model.note.NoteType
import javax.inject.Inject

class NoteRemoteDataSource @Inject constructor(private val noteService: NoteService) :
    BaseApiResponse() {

    suspend fun getNotes() = safeApiCall { noteService.getNotes() }
    suspend fun getNote(id: Int) = safeApiCall { noteService.getNote(id) }
    suspend fun updateNote(note: NoteDTO) =
        safeApiCall { noteService.updateNote(note) }

    suspend fun rateNote(id: Int, rating: Int) =
        safeApiCall { noteService.rateNote(id, rating) }


    suspend fun orderNote(asc: Boolean) = safeApiCall { noteService.orderNote(asc) }
    suspend fun deleteNote(idNote: Int) = safeApiCall { noteService.deleteNote(idNote) }


    suspend fun filterNoteByType(noteType: NoteType) = safeApiCall { noteService.filterNoteByType(noteType) }

    suspend fun addNote(note: NoteDTO) = safeApiCall { noteService.addNote(note) }

}