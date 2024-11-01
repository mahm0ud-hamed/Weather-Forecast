package com.example.skycast.model.sharedprefrence

import android.content.SharedPreferences
import android.util.Log
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
         val latitudeKey = "LAT_POINT"
         val longtudeKey = "LONG_POINT"
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

    override suspend fun getLatAndLingOfLocation(): Pair<Double, Double> {
        val first = sharedPrefrences.getString(latitudeKey, "0.0") ?: "0.0"
        val second = sharedPrefrences.getString(longtudeKey, "0.0")?: "0.0"
        val lat  = first.toDoubleOrNull()?: 0.0
        val long = second.toDoubleOrNull() ?: 0.0
        var points = Pair(lat, long)
        return points
    }

}