package com.example.client.domain.usecases.note.images

import android.net.Uri
import com.example.client.common.NetworkResult
import com.example.client.data.repositories.ImagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveNoteImagesUseCase @Inject constructor(private val imagesRepository: ImagesRepository) {
    fun invoke (imagesUris : List<Uri>, noteId : Int) : Flow<NetworkResult<List<Uri>>> =
        imagesRepository.saveNoteImages(imagesUris, noteId)
}