package com.example.skycast.model

import com.google.gson.annotations.SerializedName
import kotlin.collections.List

data class List(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long,
    val pop: Long,
    val sys: Sys,
    @SerializedName("dt_txt")
    val dtTxt: String,
)