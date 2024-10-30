package com.example.skycast.model.network

import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
import com.example.skycast.model.network.RetrofitHelper.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequests {
    @GET("forecast")
    suspend fun getFiveDayForeCast(
        @Query("lat")lat :Double ,
        @Query("lon") lon : Double,
        @Query("lang") lang:String ,
        @Query("units")unit:String ,
        @Query("appid") apiKey : String = API_KEY
    ): FiveDaysForeCast

    @GET("weather")
    suspend fun getCurrentWeatherData(
        @Query("lat")lat :Double ,
        @Query("lon") lon : Double,
        @Query("lang") lang:String ,
        @Query("units")unit:String ,
        @Query("appid") apiKey : String = API_KEY
    ) : CurrentWeather
}
