package com.example.client.data.remote.datasource

import com.example.client.common.NetworkResult
import com.example.client.data.remote.api_services.SocialDao
import java.util.UUID
import javax.inject.Inject

class SocialDataSource @Inject constructor(
    private val socialDao : SocialDao
) : BaseApiResponse() {
    suspend fun likeNote (noteId : Int, userId : UUID) : NetworkResult<Boolean>
    = safeApiCall { socialDao.likeNote(noteId,userId) }
}