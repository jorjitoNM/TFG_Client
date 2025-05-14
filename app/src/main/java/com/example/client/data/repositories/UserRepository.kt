package com.example.client.data.repositories

import com.example.client.domain.model.user.CredentialUser
import javax.inject.Inject

class UserRepository @Inject constructor(

) {
    suspend fun signUp (credentialUser: CredentialUser) {

    }
}