package com.example.skycast.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity

@Dao
interface WeatherDao {
    @Query("SELECT * From weather_table")
    suspend fun getAllSavedWeather():List<WeatherEntity>

    @Query("SELECT * FROM weather_table WHERE cityName = :cityName LIMIT 1")
    suspend fun getWeatherByCityName(cityName: String): WeatherEntity
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun  insertWeather (weatherEntity: WeatherEntity)
    @Delete
    suspend fun  deleteWeather(weatherEntity: WeatherEntity)
}