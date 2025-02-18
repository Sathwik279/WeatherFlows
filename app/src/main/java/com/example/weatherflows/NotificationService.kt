package com.example.weatherflows

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationService(
    private val context: Context
){
    // to get the notification service we need the context where it must be shown

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(location: String,temp: String){
        val activityIntent = Intent(context,MainActivity::class.java)

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0 // here flag immutable means what ever receives this intent cannot directly manipulate that intent
        )

        val showLocationIntent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context,
                LocationNotificationReceiver::class.java).apply{
                putExtra("location", location)
                putExtra("temp", temp)
            },
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(context, COUNTER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location Weather")
            .setContentText("The temperature at $location is $temp")
            .setContentIntent(activityPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground,
                "Weather",
                showLocationIntent
            ).build()

        notificationManager.notify(1,notification)
    }

    companion object{
        const val COUNTER_CHANNEL_ID = "counter_channel" // this is the channel to which u want to send notification to
    }
}