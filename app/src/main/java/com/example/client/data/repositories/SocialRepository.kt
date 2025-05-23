package com.example.client.data.repositories

import com.example.client.common.NetworkResult
import com.example.client.data.remote.datasource.SocialRemoteDataSource
import com.example.client.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class SocialRepository @Inject constructor(
    private val socialRemoteDataSource: SocialRemoteDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    suspend fun getSavedNotes() = withContext(dispatcher) {
        try {
            socialRemoteDataSource.getSavedNotes()
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun favNote(id: Int, username: String) = withContext(dispatcher) {
        try {
            socialRemoteDataSource.favNote(id, username)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }

    suspend fun likeNote (noteId : Int, userId : UUID)
            = socialRemoteDataSource.likeNote(noteId,userId)


}