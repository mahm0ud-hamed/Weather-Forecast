package com.example.skycast.view.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object AlarmManagerHelper {

    fun setAlarm(context: Context, triggerAtMillis: Long, weatherInfo: String) {
        val alarmIntent = Intent(context, AlarmReciver::class.java).apply {
            putExtra("weather_info", weatherInfo)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            triggerAtMillis.toInt(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }
}