package com.example.client.data.remote.datasource

import com.example.client.common.NetworkResult
import com.example.client.data.remote.service.SocialService
import javax.inject.Inject

class SocialRemoteDataSource @Inject constructor(private val socialService: SocialService) :
    BaseApiResponse() {

    suspend fun getNotes() = safeApiCall { socialService.getNotes() }

    suspend fun favNote(id: Int, username: String) =
        safeApiCall { socialService.favNote(id, username) }

    suspend fun likeNote (noteId : Int) : NetworkResult<Unit>
            = safeApiCall { socialService.likeNote(noteId) }
}