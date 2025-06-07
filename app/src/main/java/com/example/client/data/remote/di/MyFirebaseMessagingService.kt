package com.example.client.data.remote.di

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Manejar el mensaje recibido
        Log.d("FirebaseMessaging", "Mensaje recibido: ${remoteMessage.data}")
    }

    override fun onNewToken(token: String) {
        // Manejar la actualización del token
        Log.d("FirebaseMessaging", "Nuevo token: $token")
    }
}