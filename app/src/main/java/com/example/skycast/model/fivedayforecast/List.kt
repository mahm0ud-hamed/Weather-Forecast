package com.example.skycast.model.fivedayforecast

import com.example.skycast.model.Main
import com.example.skycast.model.commonpojo.Weather
import com.example.skycast.model.Wind
import com.example.skycast.model.commonpojo.Clouds
import com.google.gson.annotations.SerializedName
import kotlin.collections.List

data class List(
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