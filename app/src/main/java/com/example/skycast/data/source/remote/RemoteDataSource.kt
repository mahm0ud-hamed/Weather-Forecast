package com.example.skycast.data.source.remote

import com.example.skycast.data.network.RetrofitHelper
import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.skycast.data.Result
import com.example.skycast.data.Result.Success
import com.example.skycast.data.Result.Error
class RemoteDataSource: IRemoteDataSource {
    private val apiService = RetrofitHelper.service
    override suspend fun getFiveDaysForeCast(lat: Double, lon: Double, lan :String , unit: String):Result<Flow<FiveDaysForeCast>> {
        /*try to get data over network , if succeeded return data , else return error  */
        return  try {
            val result = apiService.getFiveDayForeCast(lat , lon, lan , unit )
            val flowResult = flow {emit(result)  }
            Success(flowResult)
        } catch (e :Exception){
            Error(e)
        }
    }

    /*calling method that will return the current weather state*/
    override suspend  fun getCurrentWeatherState(lat: Double, lon: Double, lan :String , unit: String): Result<Flow<CurrentWeather>> {
        /*calling the method that will return the current weather satate */
        return try{
            val result = apiService.getCurrentWeatherData(lat , lon ,lan , unit)
            var flowResult = flow{ emit (result)}
            Success(flowResult)
        }catch (e :Exception){
            Error(e)
        }
    }
}