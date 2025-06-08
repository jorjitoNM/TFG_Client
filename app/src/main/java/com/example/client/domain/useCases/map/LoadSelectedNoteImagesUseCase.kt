package com.example.client.domain.usecases.map

import com.example.client.common.NetworkResult
import com.example.client.data.model.NoteDTO
import com.example.client.data.repositories.ImagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadSelectedNoteImagesUseCase @Inject constructor(
    private val imagesRepository: ImagesRepository
) {
    fun invoke (notes : List<NoteDTO>) : Flow<NetworkResult<List<NoteDTO>>> =
        imagesRepository.loadSelectedNoteImagesUseCase(notes)
}