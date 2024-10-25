package com.example.skycast

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skycast.Util.dayOneEnd
import com.example.skycast.Util.dayOneStart
import com.example.skycast.Util.getTimeAsHumanRedable
import com.example.skycast.data.Result
import com.example.skycast.databinding.ActivityMainBinding
import com.example.skycast.data.repository.Repository
import com.example.skycast.data.source.remote.RemoteDataSource
import com.example.skycast.model.Main
import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.view.homeview.DayDetailsAdapter
import com.example.skycast.view.homeview.HomeViewModel
import com.example.skycast.view.homeview.HomeVmFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.skycast.model.fivedayforecast.List

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
      //  enableEdgeToEdge()
        setContentView(binding.root)

        /*creating an object from view model*/
        val vmFactory = HomeVmFactory(Repository(RemoteDataSource()))
        val viewModel = ViewModelProvider(this , vmFactory).get(HomeViewModel::class.java)

        val dayDetailsAdapter = DayDetailsAdapter(arrayListOf())
        binding.rvWholeDayStates.apply {
            adapter = dayDetailsAdapter
            layoutManager =LinearLayoutManager(this@MainActivity).apply { orientation = RecyclerView.HORIZONTAL }
        }

        /*request five days forecast*/
        viewModel.getFiveDaysForeCast(lat = 30.4182 , lon = 30.5747 ,lan="en", unit = "Metric")
        lifecycleScope.launch {
            viewModel.fiveDaysForeCast.collect{
                when(it){
                    is Result.Error -> Log.i("data" , "fail")
                    Result.Loading -> Log.i("data" , "Loading")
                    is Result.Success -> it.data.collect{
                        /*setting the max and minimum temperature in the five day forecast */
                        val dayone = getMaxAndMinTemperaturePerDay(it.list.subList(0,7))
                        val dayTwo = getMaxAndMinTemperaturePerDay(it.list.subList(8 ,15))
                        val daythree = getMaxAndMinTemperaturePerDay(it.list.subList(16 ,23))
                        val dayFour = getMaxAndMinTemperaturePerDay(it.list.subList(24,31))
                        val dayFive= getMaxAndMinTemperaturePerDay(it.list.subList(32 ,39))

                        binding.tvFdTodayTemp1.text= dayone.first.toString()
                        binding.tvFdTodayTemp2.text= " /${dayone.second} \u2103"
                        binding.tvFdSecondTemp1.text= dayTwo.first.toString()
                        binding.tvFdSecondTemp2.text= " /${dayTwo.second} \u2103"
                        binding.tvFdThirdTemp1.text= daythree.first.toString()
                        binding.tvFdThirdTemp2.text= " /${ daythree.second } \u2103"
                        binding.tvFdFourthTemp1.text= dayFour.first.toString()
                        binding.tvFdFourthTemp2.text= " /${ dayFour.second } \u2103"
                        binding.tvFdFifthTemp1.text= dayFive.first.toString()
                        binding.tvFdFifthTemp2.text= " /${ dayFive.second } \u2103"


                        binding.tvFdSecondDay.text = convertDateToHmanRedable(it.list.get(8).dt.toLong()).second
                        binding.tvFdThirdDay.text = convertDateToHmanRedable(it.list.get(16).dt.toLong()).second
                        binding.tvFdFourthDay.text = convertDateToHmanRedable(it.list.get(24).dt.toLong()).second
                        binding.tvFdFifthDay.text = convertDateToHmanRedable(it.list.get(32).dt.toLong()).second

                        setWeatherStateImage(binding.imgvFdTody,it.list.get(0).weather.get(0).icon)
                        setWeatherStateImage(binding.imgvFdSecondDay,it.list.get(9).weather.get(0).icon)
                        setWeatherStateImage(binding.imgvFdThirdDay,it.list.get(16).weather.get(0).icon)
                        setWeatherStateImage(binding.imgvFdFourthDay,it.list.get(24).weather.get(0).icon)
                        setWeatherStateImage(binding.imgvFdFifthDay,it.list.get(35).weather.get(0).icon)

                        binding.tvFdTodaySkyState.text = it.list.get(0).weather[0].main
                        binding.tvFdSecondSkyState.text = it.list.get(8).weather[0].main
                        binding.tvFdThirdSkyState.text = it.list.get(16).weather[0].main
                        binding.tvFdFouthSkyState.text = it.list.get(23).weather[0].main
                        binding.tvFdFifthSkyState.text = it.list.get(32).weather[0].main




                        dayDetailsAdapter.updateList(it.list.subList(dayOneStart, dayOneEnd))
                        dayDetailsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }


        /*request the current weather data */
        viewModel.getCurrentWeatherState(lat = 30.4182 , lon = 30.5747 ,lan="en", unit = "Metric" )
        lifecycleScope.launch {
            viewModel.currentWeatherState.collect{
                when(it){
                    is Result.Error -> it.exception.toString()
                    is Result.Loading -> Log.i("data" , "loading")
                    is Result.Success -> it.data.collect {
                        /*set country name */
                        setCurrentDayWeatherDetalis(it , binding)
                    }
                }
            }
        }
    }
}


/*method too check the minimum and mximum temperature per day , return Pair of int with m,min and max value */

fun getMaxAndMinTemperaturePerDay(dayInfo:kotlin.collections.List<List<Any?>>):Pair<Int , Int>{
    var maxTemps : MutableList<Int> = mutableListOf()
    var minTemps : MutableList<Int> = mutableListOf()
    for (i in  0 until dayInfo.size ){
        /*compare each element and get maximum value */
        maxTemps.add(i,dayInfo.get(i).main.tempMax.toInt())
        minTemps.add(i,dayInfo.get(i).main.tempMin.toInt())
    }
    /*return the max Temperature degree of the list and the minmum degree of list */
    return Pair(maxTemps.max(),minTemps.min())
}

fun setCurrentDayWeatherDetalis(info : CurrentWeather , binding :ActivityMainBinding){
    setCityName(info , binding.tvCityName)
    setDate(info , binding.tvDate)
    setWeatherStateImage(binding.imgvWeatherState,info.weather.get(0).icon)
    setWeatherDescription(info , binding.tvSkyState)
    setCurrentTemperature(info ,binding.tvCurrentTemp)
    binding.tvPressureValue.text = info.main.pressure.toString()
    binding.tvHumidityValue.text= info.main.humidity.toString()
    binding.tvWindSpeedValue.text=info.wind.speed.toString()
    binding.tvCloudValue.text=info.clouds.all.toString()
    binding.tvSunRiseTime.text= getTimeAsHumanRedable(info.sys.sunrise.toLong())
    binding.tvSunsetTime.text= getTimeAsHumanRedable(info.sys.sunset .toLong())

}
 fun setCityName(info :CurrentWeather, tvCityname : TextView){
     tvCityname.text =" ${info.sys.country} , ${info.name}"
 }
fun setDate(info: CurrentWeather , tvDate :TextView){
    tvDate.text= convertDateToHmanRedable(info.dt).first
    Log.i("day name " ,convertDateToHmanRedable(info.dt).second )
}

/*this method return pair of string , first was the date as YYYY-MM-dd and second was the day name Saturday*/
fun convertDateToHmanRedable( unixTime : Long):Pair<String, String>{
    val date = Date(unixTime *1000)
    val format = SimpleDateFormat("yyyy-MM-dd " ,Locale.getDefault())
    val dayName =  SimpleDateFormat("EEEE" ,Locale.getDefault())
    return Pair(format.format(date),dayName.format(date))
}


fun setWeatherStateImage(imgv: ImageView, info: String){
    var imgThumbnail = "https://openweathermap.org/img/wn/"
    Glide.with(imgv.context).load("${imgThumbnail}${info}@2x.png").into(imgv)
}

fun setWeatherDescription(info :CurrentWeather,tvDescription :TextView){
    tvDescription.text = info.weather.get(0).description
}

/*wait for edit to select the degree descriptor */
fun setCurrentTemperature(info :CurrentWeather , tvCurrentDegree:TextView){

    tvCurrentDegree.text = "${info.main.temp.toInt()} ${if (1 == 1){"\u2103"}else {"hello"}}"
}