package com.example.skycast.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.skycast.model.pojo.alarmentity.WeatherAlarm

@Dao
interface AlarmDao {
    @Insert
    suspend fun insertAlarm(alarm: WeatherAlarm)

    @Query("SELECT * FROM alarms_table")
    suspend fun getAllAlarms(): List<WeatherAlarm>

    @Query("DELETE FROM alarms_table WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId: Long)
}