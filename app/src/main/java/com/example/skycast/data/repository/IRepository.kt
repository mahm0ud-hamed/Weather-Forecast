package com.example.skycast.data.repository

import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow
import com.example.skycast.data.Result
import com.example.skycast.model.pojo.alarmentity.WeatherAlarm
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity

interface IRepository {
    /*method to get data from remote dat source*/
    suspend fun getRemoteFiveDaysForeCast(
        lat: Double,
        lon: Double,
        lan: String,
        unit: String
    ): Flow<Result<FiveDaysForeCast>>

    suspend fun getRemoteCurrentWeatherState(
        lat: Double,
        lon: Double,
        lan: String,
        unit: String
    ): Flow<Result<CurrentWeather>>

    /*dealing with shared prefrences*/
    suspend fun saveSelection(key: String, value: String)
    suspend fun loadTemperatureUnit(): Flow<Result<String>>
    suspend fun loadLanguage(): Flow<Result<String>>
    suspend fun loadWindSpeedUnit(): Flow<Result<String>>
    suspend fun loadLocationDetection():String

    suspend fun loadLatAndLongOfLocation():Flow<Result<Pair<Double,Double>>>


    /*data base functions */
    suspend fun getAllSavedLocations(): Flow<Result<List<WeatherEntity>>>
    suspend fun getSavedLocationByCityName(cityName : String):Flow<Result<WeatherEntity>>
    suspend fun saveLocation(weatherEntity: WeatherEntity)
    suspend fun deleteLocation(weatherEntity: WeatherEntity)

    suspend fun savaAlarm(alarm: WeatherAlarm)
    suspend fun getAllAlarms() : Flow<Result<List<WeatherAlarm>>>
    suspend fun deleteAlams(id : Long)

}