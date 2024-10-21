package com.example.skycast.network

import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    suspend fun getFiveDaysForeCast(lat :Double , lon : Double):Flow<FiveDaysForeCast>
    suspend fun getCurrentWeatherState(lat :Double , lon: Double) :Flow<CurrentWeather>
}