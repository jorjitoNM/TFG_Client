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

    @GET("notes/area")
    suspend fun getNotesByArea(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double): Response<List<NoteDTO>>

    @PUT("notes")
    suspend fun updateNote(
        @Body note: NoteDTO,
        @Header("X-Username") username: String
    ): Response<NoteDTO>

    @PATCH("notes/{id}/rate")
    suspend fun rateNote(
        @Path("id") id: Int,
        @Query("rating") rating: Int,
        @Header("X-Username") username: String
    ): Response<NoteDTO>

    
}