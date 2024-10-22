package com.example.skycast.data.source.remote

import com.example.skycast.data.Result
import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow
interface IRemoteDataSource {
    suspend fun getFiveDaysForeCast(lat :Double , lon : Double , lan :String , unit: String):Result<Flow<FiveDaysForeCast>>
    suspend fun getCurrentWeatherState(lat :Double , lon: Double, lan :String , unit: String) :Result<Flow<CurrentWeather>>
}