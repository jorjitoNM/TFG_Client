package com.example.client.data.remote.service

import com.example.client.domain.model.note.NoteType
import com.example.client.ui.noteScreen.list.NoteDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NoteService {
    @GET("/notes")
    suspend fun getNotes(): Response<List<NoteDTO>>

    @GET("/notes/{noteId}")
    suspend fun getNote(@Path("noteId") id: Int): Response<NoteDTO>

    @PUT("/notes")
    suspend fun updateNote(
        @Body note: NoteDTO,
        @Header("X-Username") username: String
    ): Response<NoteDTO>

    @PATCH("/notes/{id}/rate")
    suspend fun rateNote(
        @Path("id") id: Int,
        @Query("rating") rating: Int,
        @Header("X-Username") username: String
    ): Response<NoteDTO>

    @POST("/notes/saveds")
    suspend fun favNote(
        @Query("noteId") noteId: Int,
        @Query("username") username: String
    ): Response<Unit>

    @GET("/notes/sorted")
    suspend fun orderNote(
        @Query("ascending") asc: Boolean
    ): Response<List<NoteDTO>>


    @GET("/notes/type")
    suspend fun filterNoteByType(
        @Query("type") noteType: NoteType,
    ): Response<List<NoteDTO>>

}