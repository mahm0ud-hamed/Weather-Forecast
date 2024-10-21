package com.example.skycast.model.repository

import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import com.example.skycast.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class Repository (private val remoteDataSrc : RemoteDataSource ):IRepository {
    override suspend fun getRemoteFiveDaysForeCast(lat :Double , lon :Double): Flow<FiveDaysForeCast> {
      var result =  remoteDataSrc.getFiveDaysForeCast(lat , lon)
        return result
    }

    override suspend  fun getRemoteCurrentWeatherState(lat :Double , lon :Double): Flow<CurrentWeather> {
       var result =  remoteDataSrc.getCurrentWeatherState(lat , lon)
        return  result
    }
}