package com.example.skycast.network

import com.example.skycast.model.Root
import retrofit2.http.GET

interface ApiRequests {
    @GET("forecast?lat=32.364361&lon=30.506901&appid=53d6c162bb4aee864c5e8a44b4662fb6")
    suspend fun getWatherSatateByLongAndLat(): Root
}
