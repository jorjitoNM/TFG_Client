package com.example.client.data.firebase.auth

import com.example.client.domain.model.user.AuthenticationUser
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthenticator @Inject constructor() {
    private lateinit var firebaseAuth: FirebaseAuth

    fun isAuthenticated () : Boolean {
        return firebaseAuth.currentUser != null;
    }

    fun login(authenticationUser: AuthenticationUser) {
        firebaseAuth.signInWithEmailAndPassword(authenticationUser.email,authenticationUser.password)
    }

    fun register(authenticationUser: AuthenticationUser) {
        firebaseAuth.createUserWithEmailAndPassword(authenticationUser.email,authenticationUser.password)
    }
}