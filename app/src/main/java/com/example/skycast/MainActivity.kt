package com.example.skycast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.Util.convertDateToHmanRedable
import com.example.skycast.Util.dayOneEnd
import com.example.skycast.Util.dayOneStart
import com.example.skycast.Util.getTimeAsHumanRedable
import com.example.skycast.Util.setWeatherStateImage
import com.example.skycast.data.Result
import com.example.skycast.databinding.ActivityMainBinding
import com.example.skycast.data.repository.Repository
import com.example.skycast.data.source.remote.RemoteDataSource
import com.example.skycast.model.current.CurrentWeather
import com.example.skycast.model.fivedayforecast.FiveDaysForeCast
import com.example.skycast.view.homeview.DayDetailsAdapter
import com.example.skycast.view.homeview.HomeViewModel
import com.example.skycast.view.homeview.HomeVmFactory
import kotlinx.coroutines.launch
import com.example.skycast.model.fivedayforecast.List
import com.example.skycast.view.setting.Setting
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val sharedPrefFile = "SettingPref"
    val tempUnitKey = "tempUnit"
    var tempetatureUnit : String = ""
    var language :String = " "
    val languageKey = "language"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
      //  enableEdgeToEdge()
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        val sharedPrefrences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        tempetatureUnit= sharedPrefrences.getString(tempUnitKey , "standard").toString()
        language= sharedPrefrences.getString(languageKey , "en").toString()
        Log.i("language" , language)

    }
    override fun onResume() {
        /*creating an object from view model*/
        val vmFactory = HomeVmFactory(Repository(RemoteDataSource()))
        val viewModel = ViewModelProvider(this , vmFactory).get(HomeViewModel::class.java)

        val oneDayForeCastAdapter = DayDetailsAdapter(arrayListOf())
        binding.rvWholeDayStates.apply {
            adapter = oneDayForeCastAdapter
            layoutManager =LinearLayoutManager(this@MainActivity).apply { orientation = RecyclerView.HORIZONTAL }
        }

        /*request five days forecast*/
        viewModel.getFiveDaysForeCast(lat = 30.4182 , lon = 30.5747 ,lan=language, unit = tempetatureUnit)
        Log.i("language" , "after calling api "+language)
        lifecycleScope.launch {
            viewModel.fiveDaysForeCast.collect{
                when(it){
                    is Result.Error -> Log.i("data" , "fail")
                    Result.Loading -> Log.i("data" , "Loading")
                    is Result.Success -> it.data.collect{

                        setFiveDaysForecastData(it , binding)
                        oneDayForeCastAdapter.updateList(it.list.subList(dayOneStart, dayOneEnd))
                        oneDayForeCastAdapter.notifyDataSetChanged()
                    }
                }
            }
        }


        /*request the current weather data */
        viewModel.getCurrentWeatherState(lat = 30.4182 , lon = 30.5747 ,lan=language, unit = tempetatureUnit )

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
        super.onResume()
        binding.btnSetting.setOnClickListener{
            val intent = Intent(this , Setting::class.java)
            startActivity(intent)
        }
    }
}


/*method that will load all five days fore cast info in home screen */
fun setFiveDaysForecastData(info : FiveDaysForeCast, binding : ActivityMainBinding){
    /*setting the max and minimum temperature in the five day forecast */
    val dayone = getMaxAndMinTemperaturePerDay(info.list.subList(0,7))
    val dayTwo = getMaxAndMinTemperaturePerDay(info.list.subList(8 ,15))
    val daythree = getMaxAndMinTemperaturePerDay(info.list.subList(16 ,23))
    val dayFour = getMaxAndMinTemperaturePerDay(info.list.subList(24,31))
    val dayFive= getMaxAndMinTemperaturePerDay(info.list.subList(32 ,39))

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



    binding.tvFdSecondDay.text = convertDateToHmanRedable(info.list.get(8).dt.toLong()).second
    binding.tvFdThirdDay.text = convertDateToHmanRedable(info.list.get(16).dt.toLong()).second
    binding.tvFdFourthDay.text = convertDateToHmanRedable(info.list.get(24).dt.toLong()).second
    binding.tvFdFifthDay.text = convertDateToHmanRedable(info.list.get(32).dt.toLong()).second

    setWeatherStateImage(binding.imgvFdTody,info.list.get(0).weather.get(0).icon)
    setWeatherStateImage(binding.imgvFdSecondDay,info.list.get(9).weather.get(0).icon)
    setWeatherStateImage(binding.imgvFdThirdDay,info.list.get(16).weather.get(0).icon)
    setWeatherStateImage(binding.imgvFdFourthDay,info.list.get(24).weather.get(0).icon)
    setWeatherStateImage(binding.imgvFdFifthDay,info.list.get(35).weather.get(0).icon)


    setWeatherDescription(info.list.get(0).weather[0].main , binding.tvFdTodaySkyState)
    setWeatherDescription(info.list.get(8).weather[0].main ,binding.tvFdSecondSkyState)
    setWeatherDescription(info.list.get(16).weather[0].main ,binding.tvFdThirdSkyState)
    setWeatherDescription(info.list.get(23).weather[0].main ,binding.tvFdFouthSkyState)
    setWeatherDescription(info.list.get(32).weather[0].main ,binding.tvFdFifthSkyState)

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

/*method that will load info of current weather fore cast on home screen */
fun setCurrentDayWeatherDetalis(info : CurrentWeather , binding :ActivityMainBinding){
    setCityName(info , binding.tvCityName)
    setDate(info , binding.tvDate)
    setWeatherStateImage(binding.imgvWeatherState,info.weather.get(0).icon)
    setWeatherDescription(info.weather.get(0).main , binding.tvSkyState)
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
/*method to set date of current day  */
fun setDate(info: CurrentWeather , tvDate :TextView){
    tvDate.text= convertDateToHmanRedable(info.dt).first
}

fun setWeatherDescription(info: String, tvDescription:TextView){
    tvDescription.text = info
}

/*wait for edit to select the degree descriptor */
fun setCurrentTemperature(info :CurrentWeather , tvCurrentDegree:TextView){

    tvCurrentDegree.text = "${info.main.temp.toInt()} ${if (1 == 1){"\u2103"}else {"hello"}}"
}