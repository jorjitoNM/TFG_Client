package com.example.client.domain.usecases.note.images

import android.net.Uri
import com.example.client.data.repositories.ImagesRepository
import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(
    private val imagesRepository: ImagesRepository
) {
    suspend fun invoke (imageUri : Uri, noteId : Int) =
        imagesRepository.deleteImage(imageUri, noteId)
}