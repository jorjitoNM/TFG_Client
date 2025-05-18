package com.example.client.data.remote.service

import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("notes/sorted")
    suspend fun orderNote(
        @Query("ascending") asc: Boolean
    ): Response<List<NoteDTO>>


    @GET("notes/type")
    suspend fun filterNoteByType(
        @Query("type") noteType: NoteType,
    ): Response<List<NoteDTO>>

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Int):Response<Unit>


}