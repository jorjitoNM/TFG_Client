package com.example.client.data.remote.service
import com.example.client.data.model.UserDTO
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FollowService {

    @POST("users/follow")
    suspend fun followUser(
        @Query("username") username: String
    ): Response<Unit>

    @DELETE("users/unfollow")
    suspend fun unfollowUser(
        @Query("username") username: String
    ): Response<Unit>

    @GET("users/{username}/followers")
    suspend fun getFollowers(
        @Path("username") username: String
    ): Response<List<UserDTO>>

    @GET("users/{username}/following")
    suspend fun getFollowing(
        @Path("username") username: String
    ): Response<List<UserDTO>>

    @GET("users/followers")
    suspend fun getMyFollowers(): Response<List<UserDTO>>

    @GET("users/following")
    suspend fun getMyFollowing(): Response<List<UserDTO>>

    @GET("users/is-following")
    suspend fun isFollowing(
        @Query("username") username: String
    ): Response<Boolean>
}
