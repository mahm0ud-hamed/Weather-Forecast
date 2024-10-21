package com.example.skycast.network

import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import com.example.skycast.network.RetrofitHelper.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequests {
    @GET("forecast")
    suspend fun getFiveDayForeCast(
        @Query("lat")lat :Double ,
        @Query("lon") lon : Double,
        @Query("appid") apiKey : String = API_KEY
    ): FiveDaysForeCast

    @GET("weather")
    suspend fun getCurrentWeatherData(
        @Query("lat")lat :Double ,
        @Query("lon") lon : Double,
        @Query("appid") apiKey : String = API_KEY
    ) : CurrentWeather
}
