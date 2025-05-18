package com.example.client.domain.usecases.user

import com.example.client.data.repositories.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke(username:String) = userRepository.getUser(username)
}