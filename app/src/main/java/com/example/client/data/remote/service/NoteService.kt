package com.example.client.data.remote.service

import com.example.client.domain.model.note.Note
import com.example.client.ui.noteScreen.list.NoteDTO
import retrofit2.Response
import retrofit2.http.*

interface NoteService {
    @GET("api/notes")
    suspend fun getNotes(): Response<List<NoteDTO>>

    @GET("api/notes/{noteId}")
    suspend fun getNote(@Path("noteId") id: Int): Response<NoteDTO>

    @PUT("api/notes")
    suspend fun updateNote(
        @Body note: NoteDTO,
        @Header("X-Username") username: String
    ): Response<NoteDTO>

    @PATCH("api/notes/{id}/rate")
    suspend fun rateNote(
        @Path("id") id: Int,
        @Query("rating") rating: Int,
        @Header("X-Username") username: String
    ): Response<NoteDTO>
}