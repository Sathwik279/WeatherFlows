package com.example.weatherflows

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocationNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val location = intent?.getStringExtra("location") ?: "Unknown"
        val temp = intent?.getStringExtra("temperature")?:"Unknown"

        val service = NotificationService(context)
        service.showNotification(location, temp)
    }
}