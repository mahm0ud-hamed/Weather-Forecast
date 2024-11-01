package com.example.skycast.model.pojo.fivedayforecast

data class FiveDaysForeCast(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: kotlin.collections.List<List>,
    val city: City,
)