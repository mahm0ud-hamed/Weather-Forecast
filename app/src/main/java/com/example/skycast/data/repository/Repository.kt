package com.example.skycast.data.repository

import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
import com.example.skycast.data.source.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import com.example.skycast.data.Result
import com.example.skycast.data.source.local.ILocalDataSource
import com.example.skycast.data.source.remote.IRemoteDataSource
import kotlinx.coroutines.flow.flow

class Repository (private val remoteDataSrc : IRemoteDataSource , private val localDataSrc :ILocalDataSource): IRepository {

    override suspend fun getRemoteFiveDaysForeCast(lat :Double, lon :Double, lan :String , unit: String): Flow<Result<FiveDaysForeCast>> {
      var result =  remoteDataSrc.getFiveDaysForeCast(lat , lon , lan , unit)
        return result
    }

    override suspend  fun getRemoteCurrentWeatherState(lat :Double , lon :Double, lan :String , unit: String): Flow<Result<CurrentWeather>> {
        return  remoteDataSrc.getCurrentWeatherState(lat , lon , lan , unit)

    }

    override suspend fun saveSelection(key: String, value: String) {
       localDataSrc.saveSelectionInSharedPref(key , value)
    }

    override suspend fun loadTemperatureUnit(): Flow<Result<String>> {
      return localDataSrc.loadSavedTemperatureUnit()
    }

    override suspend fun loadLanguage(): Flow<Result<String>> {
        return localDataSrc.loadSavedLanguage()
    }

    override suspend fun loadWindSpeedUnit(): Flow<Result<String>> {
        return localDataSrc.loadWindSpeedUnit()
    }
}