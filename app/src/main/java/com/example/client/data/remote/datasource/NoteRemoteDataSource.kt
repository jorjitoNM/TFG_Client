package com.example.client.data.remote.datasource

import com.example.client.data.remote.service.NoteService
import com.example.client.ui.noteScreen.list.NoteDTO
import javax.inject.Inject

class NoteRemoteDataSource @Inject constructor(private val noteService: NoteService) :
    BaseApiResponse() {

    suspend fun getNotes() = safeApiCall { noteService.getNotes() }

    suspend fun getNote(id: Int) = safeApiCall { noteService.getNote(id) }
    suspend fun updateNote(note: NoteDTO, username: String) =
        safeApiCall { noteService.updateNote(note, username) }

    suspend fun rateNote(id: Int, rating: Int, username: String) =
        safeApiCall { noteService.rateNote(id, rating, username) }

    suspend fun favNote(id: Int, username: String) =
        safeApiCall { noteService.favNote(id, username) }

    suspend fun orderNote(asc : Boolean) = safeApiCall { noteService.orderNote(asc) }
}