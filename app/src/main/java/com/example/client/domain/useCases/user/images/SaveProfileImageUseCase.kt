package com.example.client.domain.usecases.user.images

import android.net.Uri
import com.example.client.common.NetworkResult
import com.example.client.data.repositories.ImagesRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class SaveProfileImageUseCase @Inject constructor(
      private val imagesRepository: ImagesRepository) {
      fun invoke(image: Uri, userId: UUID): Flow<NetworkResult<Uri>> =
            imagesRepository.saveProfileImage(image,userId)
}