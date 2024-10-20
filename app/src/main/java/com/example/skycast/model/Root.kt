package com.example.skycast.model

data class Root(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: kotlin.collections.List<List>,
    val city: City,
)