package com.example.client.data.remote.datasource

import com.example.client.common.NetworkResult
import com.example.client.data.remote.service.SocialService
import javax.inject.Inject

class SocialRemoteDataSource @Inject constructor(private val socialService: SocialService) :
    BaseApiResponse() {

    suspend fun getSavedNotes() = safeApiCall { socialService.getNotesSaved() }

    suspend fun favNote(id: Int) =
        safeApiCall { socialService.favNote(id) }

    suspend fun likeNote (noteId : Int) : NetworkResult<Unit>
            = safeApiCall { socialService.likeNote(noteId) }

    suspend fun delFavNote(noteId: Int) =
        safeApiCallNoBody { socialService.deleteSavedNote(noteId) }

    suspend fun delLikeNote(noteId: Int) =
        safeApiCallNoBody { socialService.deleteLikeNote(noteId) }
}