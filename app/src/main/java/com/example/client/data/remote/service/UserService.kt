package com.example.client.data.remote.service


import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
    @GET("user")
    suspend fun getUser(): Response<UserDTO>

    @GET("user/notes")
    suspend fun getMyNotes(): Response<List<NoteDTO>>

    @GET("user/notes/{username}")
    suspend fun getUserNotes(@Query("username") username: String): Response<List<NoteDTO>>

    @GET("user/all")
    suspend fun getAllUserStartsWithText(@Query("text") text: String): Response<List<UserDTO>>

    @GET("user/likes")
    suspend fun getLikedNotes(): Response<List<NoteDTO>>

    data class FirebaseIdResponse(val firebaseId: String)

    @GET("/user/firebase_id")
    suspend fun getFirebaseId(): Response<FirebaseIdResponse>

    @GET("user/info/{username}")
    suspend fun getUserInfo(@Query("username") username: String): Response<UserDTO>
}