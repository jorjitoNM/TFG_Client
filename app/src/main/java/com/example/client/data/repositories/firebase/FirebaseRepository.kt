package com.example.client.data.repositories.firebase

import com.example.client.data.firebase.auth.FirebaseAuthenticator
import com.example.client.domain.model.user.AuthenticationUser
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseAuthenticator: FirebaseAuthenticator
) {
    fun register(authenticationUser: AuthenticationUser) : Unit =
        firebaseAuthenticator.register(authenticationUser)

    fun login(authenticationUser: AuthenticationUser) : Unit =
        firebaseAuthenticator.login(authenticationUser)

}