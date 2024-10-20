package com.example.skycast.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private var  baseUrl :String ="https://api.openweathermap.org/data/2.5/"
    private  val serviceRequest = Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val  service = serviceRequest.create(ApiRequests::class.java)
}