package com.example.skycast.data.repository

import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow
import com.example.skycast.data.Result

interface IRepository {
    /*method to get data from remote dat source*/
    suspend fun getRemoteFiveDaysForeCast(lat :Double , lon :Double , lan :String , unit: String ): Result<Flow<FiveDaysForeCast>>
   suspend fun getRemoteCurrentWeatherState(lat :Double , lon :Double, lan :String , unit: String):Result<Flow<CurrentWeather>>
}