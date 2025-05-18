package com.example.client.data.remote.service

import com.example.client.data.model.NoteDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SocialService {

    @POST("social/saveds")
    suspend fun favNote(
        @Query("noteId") noteId: Int,
        @Query("username") username: String
    ): Response<Unit>

    @GET("notes/saveds?username=user1")
    suspend fun getNotes(): Response<List<NoteDTO>>

    @FormUrlEncoded
    @POST("social/like")
    suspend fun likeNote (@Body noteId : Int) : Response<Unit>
}