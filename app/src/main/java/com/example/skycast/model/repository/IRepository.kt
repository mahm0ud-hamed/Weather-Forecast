package com.example.skycast.model.repository

import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow

interface IRepository {
    /*method to get data from remote dat source*/
    suspend fun getRemoteFiveDaysForeCast(lat :Double , lon :Double ): Flow<FiveDaysForeCast>
   suspend fun getRemoteCurrentWeatherState(lat :Double , lon :Double):Flow<CurrentWeather>
}