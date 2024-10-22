package com.example.skycast.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val   BASE_URL  ="https://api.openweathermap.org/data/2.5/"
    val   API_KEY   =  "53d6c162bb4aee864c5e8a44b4662fb6"
    private  val serviceRequest = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val  service = serviceRequest.create(ApiRequests::class.java)
}