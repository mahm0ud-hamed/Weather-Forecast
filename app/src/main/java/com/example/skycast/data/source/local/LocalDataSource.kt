package com.example.skycast.data.source.local

import android.util.Log
import com.example.skycast.data.Result
import com.example.skycast.model.database.WeatherDao
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity
import com.example.skycast.model.sharedprefrence.ISharedPrefrenceHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalDataSource(val sharedPrefHelper: ISharedPrefrenceHelper , val weatherDao: WeatherDao) : ILocalDataSource {
    override suspend fun saveSelectionInSharedPref(key: String, value: String) {
        sharedPrefHelper.saveSelection(key, value)
    }

    override suspend fun loadSavedTemperatureUnit(): Flow<Result<String>> = flow {
        try {
            val tempUnit = sharedPrefHelper.laodTemperatureUnit()
            emit(Result.Success(tempUnit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun loadSavedLanguage(): Flow<Result<String>> = flow {
        try {
            val language = sharedPrefHelper.loadLanguage()
            emit(Result.Success(language))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun loadWindSpeedUnit(): Flow<Result<String>> = flow {
        try {
            val seedUnit = sharedPrefHelper.loadWindSpeedUnit()
            emit(Result.Success(seedUnit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun loadLatAndLongOfLocation(): Flow<Result<Pair<Double, Double>>> = flow{
        try {
            val points = sharedPrefHelper.getLatAndLingOfLocation()
            Log.i("intent" , "from Helper"+points.first.toString())
            emit(Result.Success(points))
        }catch (e :Exception){

            emit(Result.Error(e))
        }
    }

    override suspend fun saveLocation(weatherEntity: WeatherEntity) {
       weatherDao.insertWeather(weatherEntity)
    }

    override suspend fun getAllSavedLocations(): Flow<Result<List<WeatherEntity>>> = flow {
       try {
           val savedLocations = weatherDao.getAllSavedWeather()
           emit(Result.Success(savedLocations))
       }catch (e :Exception){
          emit(Result.Error(e))
       }
    }

    override suspend fun getSavedLocationByCityName(cityName: String): Flow<Result<WeatherEntity>> = flow{

        try {
            val location = weatherDao.getWeatherByCityName(cityName)
            emit(Result.Success(location))
        }catch (e :Exception){
            emit(Result.Error(e))
        }
    }

    override suspend fun deleteSavedLocation(weatherEntity: WeatherEntity) {
       weatherDao.deleteWeather(weatherEntity)
    }
}