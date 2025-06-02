package com.example.client.data.firebase.auth

import com.example.client.domain.model.user.AuthenticationUser
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthenticator {
    private lateinit var firebaseAuth: FirebaseAuth

    fun isAuthenticated () : Boolean {
        return firebaseAuth.currentUser != null;
    }

    fun authenticate(authenticationUser: AuthenticationUser) {
        firebaseAuth.signInWithEmailAndPassword(authenticationUser.username,authenticationUser.password)
    }
}