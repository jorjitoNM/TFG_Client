package com.example.client.data.remote.service

import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.Note

import retrofit2.Response
import retrofit2.http.*

interface NoteService {
    @GET("notes")
    suspend fun getNotes(): Response<List<NoteDTO>>

    @GET("notes/{noteId}")
    suspend fun getNote(@Path("noteId") id: Int): Response<NoteDTO>

    @PUT("notes")
    suspend fun updateNote(
        @Body note: NoteDTO,
    ): Response<NoteDTO>

    @PATCH("notes/{id}/rate")
    suspend fun rateNote(
        @Path("id") id: Int,
        @Query("rating") rating: Int,
    ): Response<NoteDTO>

    
}