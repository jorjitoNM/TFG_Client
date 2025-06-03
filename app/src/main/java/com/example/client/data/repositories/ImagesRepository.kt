package com.example.client.data.repositories

import android.net.Uri
import com.example.client.R
import com.example.client.common.NetworkResult
import com.example.client.common.StringProvider
import com.example.client.di.IoDispatcher
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ImagesRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val stringProvider: StringProvider,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    fun loadNoteImages(imagesUris: List<Uri>,firebaseId : String): Flow<NetworkResult<List<String>>> = flow {
        emit(NetworkResult.Loading())
        val result = imagesUris.map { uri ->
            val imageRef = storage.reference.child( "${stringProvider.getString(R.string.fb_storage_images_url)}/${firebaseId}")
            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        }
        emit(NetworkResult.Success(result))
    }.catch { e ->
        emit(NetworkResult.Error(e.message ?: stringProvider.getString(R.string.error_uploading_images)))
    }
        .flowOn(dispatcher)
}