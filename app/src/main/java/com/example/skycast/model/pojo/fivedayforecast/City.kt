package com.example.skycast.model.pojo.fivedayforecast

import com.example.skycast.model.pojo.commonpojo.Coord

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Int,
    val sunset: Int,
)