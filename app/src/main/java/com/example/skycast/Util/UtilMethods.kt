package com.example.skycast.Util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val dayOneStart = 0
const val dayOneEnd = 7
const val dayTwoStart = 8
const val dayTwoEnd = 15



fun getTimeAsHumanRedable(unixTime : Long):String{
    val date = Date(unixTime * 1000)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}
