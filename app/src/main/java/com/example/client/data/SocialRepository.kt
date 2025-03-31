package com.example.client.data

import com.example.client.data.remote.datasource.SocialDataSource
import java.util.UUID
import javax.inject.Inject

class SocialRepository @Inject constructor(
    private val socialDatasource : SocialDataSource
) {
    suspend fun likeNote (noteId : Int, userId : UUID)
    = socialDatasource.likeNote(noteId,userId)
}