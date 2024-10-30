package com.example.skycast.data.source.remote

import com.example.skycast.model.network.RetrofitHelper
import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.skycast.data.Result
import com.example.skycast.data.Result.Success
import com.example.skycast.data.Result.Error
class RemoteDataSource: IRemoteDataSource {
    private val apiService = RetrofitHelper.service
    override suspend fun getFiveDaysForeCast(lat: Double, lon: Double, lan :String , unit: String):Flow<Result<FiveDaysForeCast>> =
        flow {
        /*try to get data over network , if succeeded return data , else return error  */
        try {
            val result = apiService.getFiveDayForeCast(lat , lon, lan , unit )
            emit(Success(result))
        } catch (e :Exception){
            emit(Error(e))
        }
    }

    /*calling method that will return the current weather state*/
    override suspend  fun getCurrentWeatherState(lat: Double, lon: Double, lan :String , unit: String): Flow<Result<CurrentWeather>> = flow{
        /*calling the method that will return the current weather satate */
         try{
            val result = apiService.getCurrentWeatherData(lat , lon ,lan , unit)
            emit(Success(result))
        }catch (e :Exception){
            emit(Error(e))
        }
    }


}