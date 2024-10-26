package com.example.skycast.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import com.example.skycast.data.source.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import com.example.skycast.data.Result

class Repository (private val remoteDataSrc : RemoteDataSource): IRepository {

    override suspend fun getRemoteFiveDaysForeCast(lat :Double, lon :Double, lan :String , unit: String): Result<Flow<FiveDaysForeCast>> {
      var result =  remoteDataSrc.getFiveDaysForeCast(lat , lon , lan , unit)
        return result
    }

    override suspend  fun getRemoteCurrentWeatherState(lat :Double , lon :Double, lan :String , unit: String): Result<Flow<CurrentWeather>> {
       var result =  remoteDataSrc.getCurrentWeatherState(lat , lon , lan , unit)
        return  result
    }
}