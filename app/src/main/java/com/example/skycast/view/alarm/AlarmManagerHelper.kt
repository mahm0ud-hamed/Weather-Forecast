package com.example.skycast.view.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object AlarmManagerHelper {

    fun setAlarm(context: Context, triggerAtMillis: Long, weatherInfo: String, alarmId:Int)  {
        val alarmIntent = Intent(context, AlarmReciver::class.java).apply {
            putExtra("weather_info", weatherInfo)
            putExtra("ALARM_ID", alarmId) // Add the ID to the intent
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,

            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    fun cancelAlarm(context: Context, alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReciver::class.java).apply {
            putExtra("ALARM_ID", alarmId) // Ensure you're using the same ID
        }

        // Create the PendingIntent to cancel using the same ID
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId, // Use the same ID
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Ensure the same flags are used
        )

        // Cancel the alarm
        alarmManager.cancel(pendingIntent)
    }
}