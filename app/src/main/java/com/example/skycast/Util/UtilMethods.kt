package com.example.skycast.Util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

const val dayOneStart = 0
const val dayOneEnd = 7
const val dayTwoStart = 8
const val dayTwoEnd = 15

object LocaleUtils {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        // Update the configuration for the current resources
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Set the layout direction based on the locale
        val layoutDirection = if (locale.language == "ar") {
            View.LAYOUT_DIRECTION_RTL
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
        if (context is Activity) {
            context.window.decorView.layoutDirection = layoutDirection
        }
    }
}

fun getTimeAsHumanRedable(unixTime: Long): String {
    val date = Date(unixTime * 1000)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}

/*this method return pair of string , first was the date as YYYY-MM-dd and second was the day name Saturday*/
fun convertDateToHmanRedable(unixTime: Long): Pair<String, String> {
    val date = Date(unixTime * 1000)
    val format = SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault())
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault())
    return Pair(format.format(date), dayName.format(date))
}

/*method to set images of weather states */
fun setWeatherStateImage(imgv: ImageView, info: String) {
    var imgThumbnail = "https://openweathermap.org/img/wn/"
    Glide.with(imgv.context).load("${imgThumbnail}${info}@2x.png").into(imgv)
}

fun formatMillisToDateTime(millis: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault() // Set to your desired time zone
    return dateFormat.format(millis)
}
// Function to check network availability
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}
