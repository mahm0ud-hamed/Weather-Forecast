package com.example.skycast.data.source.local

import com.example.skycast.data.Result
import com.example.skycast.model.sharedprefrence.ISharedPrefrenceHelper
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun saveSelectionInSharedPref(key :String , value:String)
    suspend fun loadSavedTemperatureUnit():Flow<Result<String>>
    suspend fun loadSavedLanguage():Flow<Result<String>>
    suspend fun loadWindSpeedUnit():Flow<Result<String>>
}