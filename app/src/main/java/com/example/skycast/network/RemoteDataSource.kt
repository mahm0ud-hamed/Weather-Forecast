package com.example.skycast.network

import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSource:IRemoteDataSource {
    private val apiService = RetrofitHelper.service
    override suspend fun getFiveDaysForeCast(lat: Double, lon: Double): Flow<FiveDaysForeCast> {
        /*calling the method that will return the 5 days forecast*/
        var  result = apiService.getFiveDayForeCast(lat , lon)
        return  flow { emit(result) }
    }

    /*calling method that will return the current weather state*/
    override suspend  fun getCurrentWeatherState(lat: Double, lon: Double): Flow<CurrentWeather> {
        /*calling the method that will return the current weather satate */
        var result = apiService.getCurrentWeatherData(lat , lon)
        return  flow { emit(result) }
    }
}