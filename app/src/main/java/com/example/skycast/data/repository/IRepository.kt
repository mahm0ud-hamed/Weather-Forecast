package com.example.skycast.data.repository

import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow
import com.example.skycast.data.Result

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
}