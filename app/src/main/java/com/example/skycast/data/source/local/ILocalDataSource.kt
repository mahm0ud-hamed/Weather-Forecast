package com.example.skycast.data.source.local

import com.example.skycast.data.Result
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity
import com.example.skycast.model.sharedprefrence.ISharedPrefrenceHelper
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun saveSelectionInSharedPref(key :String , value:String)
    suspend fun loadSavedTemperatureUnit():Flow<Result<String>>
    suspend fun loadSavedLanguage():Flow<Result<String>>
    suspend fun loadWindSpeedUnit():Flow<Result<String>>

    suspend fun loadLatAndLongOfLocation() : Flow<Result<Pair<Double ,Double>>>

    suspend fun saveLocation(weatherEntity: WeatherEntity)
    suspend fun getAllSavedLocations():Flow<Result<List<WeatherEntity>>>
    suspend fun getSavedLocationByCityName(cityName : String) :Flow<Result<WeatherEntity>>
    suspend fun deleteSavedLocation(weatherEntity: WeatherEntity)


}