package com.example.skycast.model.database
import android.app.LauncherActivity
import androidx.room.TypeConverter
import com.example.skycast.model.pojo.Main
import com.example.skycast.model.pojo.Wind
import com.example.skycast.model.pojo.commonpojo.Clouds
import com.example.skycast.model.pojo.commonpojo.Coord
import com.example.skycast.model.pojo.commonpojo.Weather
import com.example.skycast.model.pojo.current.Sys
import com.example.skycast.model.pojo.fivedayforecast.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TypeConverter {
    private val gson = Gson()

    // Convert List<Weather> to String
    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>?): String {
        return gson.toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(weatherListString: String?): List<Weather>? {
        val type = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(weatherListString, type)
    }

    // Convert List to String (the List class you provided)
    @TypeConverter
    fun fromListList(list: List<com.example.skycast.model.pojo.fivedayforecast.List>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toListList(listString: String?): List<com.example.skycast.model.pojo.fivedayforecast.List>? {
        val type = object : TypeToken<List<com.example.skycast.model.pojo.fivedayforecast.List>>() {}.type
        return gson.fromJson(listString, type)
    }

    // Convert Coord to String
    @TypeConverter
    fun fromCoord(coord: Coord): String {
        return gson.toJson(coord)
    }

    @TypeConverter
    fun toCoord(coordString: String): Coord {
        val type = object : TypeToken<Coord>() {}.type
        return gson.fromJson(coordString, type)
    }

    // Convert Main to String
    @TypeConverter
    fun fromMain(main: Main): String {
        return gson.toJson(main)
    }

    @TypeConverter
    fun toMain(mainString: String): Main {
        val type = object : TypeToken<Main>() {}.type
        return gson.fromJson(mainString, type)
    }

    // Convert Wind to String
    @TypeConverter
    fun fromWind(wind: Wind): String {
        return gson.toJson(wind)
    }

    @TypeConverter
    fun toWind(windString: String): Wind {
        val type = object : TypeToken<Wind>() {}.type
        return gson.fromJson(windString, type)
    }

    // Convert Clouds to String
    @TypeConverter
    fun fromClouds(clouds: Clouds): String {
        return gson.toJson(clouds)
    }

    @TypeConverter
    fun toClouds(cloudsString: String): Clouds {
        val type = object : TypeToken<Clouds>() {}.type
        return gson.fromJson(cloudsString, type)
    }

    // Convert Sys to String
    @TypeConverter
    fun fromSys(sys: Sys): String {
        return gson.toJson(sys)
    }

    @TypeConverter
    fun toSys(sysString: String): Sys {
        val type = object : TypeToken<Sys>() {}.type
        return gson.fromJson(sysString, type)
    }
}