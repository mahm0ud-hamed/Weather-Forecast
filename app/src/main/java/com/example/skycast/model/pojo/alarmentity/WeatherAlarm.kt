package com.example.skycast.model.pojo.alarmentity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms_table")
data class WeatherAlarm(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val triggerAtMillis: Long,
    val weatherInfo: String
)