package com.example.client.data.repositories

import android.net.Uri
import com.example.client.R
import com.example.client.common.NetworkResult
import com.example.client.common.StringProvider
import com.example.client.di.IoDispatcher
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ImagesRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val stringProvider: StringProvider,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    fun saveNoteImages(imagesUris: List<Uri>, noteId: Int): Flow<NetworkResult<List<Uri>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val result = imagesUris.map { uri ->
                val fileName = UUID.randomUUID().toString()
                val imageRef = storage.reference.child(
                    "${stringProvider.getString(R.string.fb_storage_images_url)}/$noteId/$fileName"
                )
                imageRef.putFile(uri).await()
                imageRef.downloadUrl.await()
            }
            emit(NetworkResult.Success(result))
        } catch (e: Exception) {
            emit(NetworkResult.Error(
                e.message ?: stringProvider.getString(R.string.error_uploading_images)
            ))
        }
    }.flowOn(dispatcher)

    fun loadNoteImages(noteId: Int): Flow<NetworkResult<List<Uri>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val noteImagesReference = storage.reference.child("${stringProvider.getString(R.string.fb_storage_images_url)}/$noteId")
            val listResult = noteImagesReference.listAll().await()
            val uris = listResult.items.map { item ->
                item.downloadUrl.await()
            }
            emit(NetworkResult.Success(uris))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: stringProvider.getString(R.string.error_loading_images)))
        }
    }

    fun deleteImage (imageUri : Uri, noteId : Int) : Flow<NetworkResult<Unit>> = flow {
        if (storage.reference.child("${stringProvider.getString(R.string.fb_storage_images_url)}/$noteId/$imageUri")
            .delete().isSuccessful)
            emit(NetworkResult.Success(Unit))
        else emit(NetworkResult.Error(stringProvider.getString(R.string.error_deleting_image)))
    }
}