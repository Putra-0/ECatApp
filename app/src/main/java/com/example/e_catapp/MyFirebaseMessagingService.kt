package com.example.e_catapp

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Menampilkan notifikasi saat pesan diterima
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        // Implementasikan tampilan notifikasi sesuai kebutuhan Anda
    }
}
