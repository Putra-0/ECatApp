package com.example.e_catapp

import android.content.Context
import android.content.Intent
import androidx.legacy.content.WakefulBroadcastReceiver

class MyFirebaseMessagingReceiver : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        // Memulai MyFirebaseMessagingService untuk menangani pesan yang diterima
        intent?.setClass(context, MyFirebaseMessagingService::class.java)
        startWakefulService(context, intent)
    }
}