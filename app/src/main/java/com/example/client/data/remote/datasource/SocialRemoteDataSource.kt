package com.example.client.data.remote.datasource

import com.example.client.data.remote.service.SocialService
import java.util.UUID
import javax.inject.Inject

class SocialRemoteDataSource @Inject constructor(private val socialService: SocialService) :
    BaseApiResponse() {

    suspend fun getSavedNotes() = safeApiCall { socialService.getNotesSaved() }

    suspend fun favNote(id: Int, username: String) =
        safeApiCall { socialService.favNote(id, username) }

    suspend fun likeNote (noteId : Int, userId : UUID)
            = safeApiCall { socialService.likeNote(noteId,userId) }
}