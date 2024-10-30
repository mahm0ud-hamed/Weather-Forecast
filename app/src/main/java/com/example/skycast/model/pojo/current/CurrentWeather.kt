package com.example.skycast.model.pojo.current

import com.example.skycast.model.pojo.commonpojo.Clouds
import com.example.skycast.model.pojo.commonpojo.Coord
import com.example.skycast.model.pojo.Main
import com.example.skycast.model.pojo.commonpojo.Weather
import com.example.skycast.model.pojo.Wind

data class CurrentWeather(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long,
)