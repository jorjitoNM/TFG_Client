package com.example.client.data.remote.service

import com.example.client.data.model.NoteDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SocialService {

    @POST("social/saveds")
    suspend fun favNote(
        @Query("noteId") noteId: Int
    ): Response<Unit>

    @GET("notes/saveds")
    suspend fun getNotesSaved(): Response<List<NoteDTO>>

    @DELETE("notes/saveds")
    suspend fun deleteSavedNote(
        @Query("noteId") noteId: Int
    ): Response<Void>

    @DELETE("notes/liked")
    suspend fun deleteLikeNote(
        @Query("noteId") noteId: Int
    ): Response<Void>

    @FormUrlEncoded
    @POST("social/like")
    suspend fun likeNote (@Body noteId : Int) : Response<Unit>
}