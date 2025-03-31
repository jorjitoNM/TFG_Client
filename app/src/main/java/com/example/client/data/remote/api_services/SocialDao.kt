package com.example.client.data.remote.api_services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface SocialDao {
    @POST("/social/like")
    fun likeNote(@Body noteId: Int,@Body userID: Any) : Response<Boolean>
}