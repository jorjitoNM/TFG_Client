package com.example.client.data.firebase.auth

import com.example.client.common.NetworkResult
import com.example.client.domain.model.user.AuthenticationUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthenticator @Inject constructor() {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    fun isAuthenticated () : Boolean {
        return firebaseAuth.currentUser != null;
    }

    suspend fun login(authenticationUser: AuthenticationUser): NetworkResult<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(authenticationUser.email, authenticationUser.password).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Success(Unit)
        }
    }

    suspend fun register(authenticationUser: AuthenticationUser): NetworkResult<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(authenticationUser.email, authenticationUser.password).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Success(Unit)
        }
    }
}