package com.example.skycast.model.pojo.weatherEntity
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.skycast.model.database.TypeConverter
import com.example.skycast.model.pojo.Main
import com.example.skycast.model.pojo.Wind
import com.example.skycast.model.pojo.commonpojo.Clouds
import com.example.skycast.model.pojo.commonpojo.Coord
import com.example.skycast.model.pojo.commonpojo.Weather
import com.example.skycast.model.pojo.current.Sys
import com.example.skycast.model.pojo.fivedayforecast.List

@Entity(tableName = "weather_table")
@TypeConverters(TypeConverter::class)
data class WeatherEntity(

    @PrimaryKey val cityName: String, // Use the city name as the primary key
    val coord: Coord,
    val weather: kotlin.collections.List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val cod: Long,
    val list: kotlin.collections.List<List>
)

