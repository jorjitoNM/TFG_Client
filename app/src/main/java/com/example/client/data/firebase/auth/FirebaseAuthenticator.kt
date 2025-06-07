package com.example.client.data.firebase.auth

import com.example.client.R
import com.example.client.common.NetworkResult
import com.example.client.common.StringProvider
import com.example.client.domain.model.user.AuthenticationUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthenticator @Inject constructor(
    private val stringProvider: StringProvider
) {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    fun isAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null;
    }

    suspend fun login(authenticationUser: AuthenticationUser): NetworkResult<Unit> =
        suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(
                authenticationUser.email,
                authenticationUser.password
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(NetworkResult.Success(Unit))
                } else {
                    val errorMessage =
                        stringProvider.getString(R.string.failed_to_authenticate_with_firebase)
                    continuation.resume(NetworkResult.Error(errorMessage))
                }
            }
        }

    suspend fun register(authenticationUser: AuthenticationUser): NetworkResult<Unit> =
        suspendCoroutine { continuation ->
            firebaseAuth.createUserWithEmailAndPassword(
                authenticationUser.email,
                authenticationUser.password
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(NetworkResult.Success(Unit))
                } else {
                    val errorMessage =
                        stringProvider.getString(R.string.failed_to_authenticate_with_firebase)
                    continuation.resume(NetworkResult.Error(errorMessage))
                }
            }
        }
}