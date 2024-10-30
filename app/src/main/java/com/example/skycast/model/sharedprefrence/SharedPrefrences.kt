package com.example.skycast.model.sharedprefrence

import android.content.SharedPreferences
import com.example.skycast.data.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SharedPrefrenceHelper(val sharedPrefrences: SharedPreferences) : ISharedPrefrenceHelper {
    companion object {
        val celsius: String = "metric"
        val kelvin: String = "standard"
        val fahrenheit: String = "imperial"
        val milePerHour = "imperial"
        val meterPerSecond = "metric"

        /*language Keys */
        val english: String = "en"
        val arabic: String = "ar"
        // Keys for SharedPreferences
         val tempUnitKey = "tempUnit"
         val languageKey = "language"
         val windSpeedKey = "tempUnit"
    }

    override suspend fun saveSelection(key: String, value: String) {
        sharedPrefrences.edit().putString(key, value).apply()
    }

    override suspend fun laodTemperatureUnit():String{

          return sharedPrefrences.getString(tempUnitKey , kelvin)?: kelvin
    }

    override suspend fun loadLanguage(): String{
        return sharedPrefrences.getString(languageKey , english)?: english
    }


    override suspend fun loadWindSpeedUnit(): String {
        return sharedPrefrences.getString(windSpeedKey , meterPerSecond) ?: milePerHour
    }

}