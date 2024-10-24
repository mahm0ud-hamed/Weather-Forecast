package com.example.skycast.model.fivedayforecast

data class FiveDaysForeCast(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: kotlin.collections.List<List<Any?>>,
    val city: City,
)