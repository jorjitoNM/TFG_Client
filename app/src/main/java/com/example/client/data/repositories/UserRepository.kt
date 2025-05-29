package com.example.client.data.repositories

import android.net.Uri
import com.example.client.R
import com.example.client.common.NetworkResult
import com.example.client.common.StringProvider
import com.example.client.data.remote.datasource.UserRemoteDataSource
import com.example.client.di.IoDispatcher
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.internal.NopCollector.emit
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val stringProvider: StringProvider,
    private val storage: FirebaseStorage,
    ) {
    suspend fun getUser() = withContext(dispatcher) {
        try {
            userRemoteDataSource.getUser("user1")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }
    suspend fun getMyNotes()= withContext(dispatcher) {
        try {
            userRemoteDataSource.getMyNotes("user1")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: e.toString())
        }
    }


    fun loadProfileImage(imagesUris: Uri): Flow<NetworkResult<String>> = flow {
        emit(NetworkResult.Loading())
        val result = imagesUris.map { uri ->
            val imageRef = storage.reference.child( "${stringProvider.getString(R.string.fb_storage_images_url)}/${UUID.randomUUID()}")
            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        }
        emit(NetworkResult.Success(result))
    }.catch { e ->
        emit(NetworkResult.Error(e.message ?: stringProvider.getString(R.string.error_uploading_images)))
    }
        .flowOn(dispatcher)
}