package com.example.skycast.model

data class Root(
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: kotlin.collections.List<List>,
    val city: City,
)