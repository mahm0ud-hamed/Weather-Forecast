package com.example.skycast.model.sharedprefrence

import com.example.skycast.data.Result
import kotlinx.coroutines.flow.Flow

interface ISharedPrefrenceHelper {

    suspend fun saveSelection(key:String , value : String)
    suspend fun laodTemperatureUnit():String
    suspend fun loadLanguage() :String
    suspend fun loadWindSpeedUnit():String
    suspend fun getLatAndLingOfLocation(): Pair<Double , Double>
    suspend fun lodaLocationDetction(): String
}