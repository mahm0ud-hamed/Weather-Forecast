package com.example.skycast.view.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.skycast.R

class AlarmReciver:BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val weatherInfo = p1?.getStringExtra("weather_info") ?: "No Weather Info"
        // Show the notification
        p0?.let { showNotification(it, weatherInfo) }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, weatherInfo: String) {
        val channelId = "weather_alarm_channel"
        val notificationId = 1

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_week_forecast) // Replace with your notification icon
            .setContentTitle("Weather Alert")
            .setContentText(weatherInfo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}