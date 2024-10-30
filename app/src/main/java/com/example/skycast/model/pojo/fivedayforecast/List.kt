package com.example.skycast.model.pojo.fivedayforecast

import com.example.skycast.model.pojo.Main
import com.example.skycast.model.pojo.commonpojo.Weather
import com.example.skycast.model.pojo.Wind
import com.example.skycast.model.pojo.commonpojo.Clouds
import com.google.gson.annotations.SerializedName
import kotlin.collections.List

data class List<T>(
    val dt: Int,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Float,
    val sys: Sys,
    @SerializedName("dt_txt")
    val dtTxt: String,
)