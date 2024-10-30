package com.example.skycast

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.Util.LocaleUtils
import com.example.skycast.Util.convertDateToHmanRedable
import com.example.skycast.Util.dayOneEnd
import com.example.skycast.Util.dayOneStart
import com.example.skycast.Util.getTimeAsHumanRedable
import com.example.skycast.Util.setWeatherStateImage
import com.example.skycast.data.Result
import com.example.skycast.databinding.ActivityMainBinding
import com.example.skycast.data.repository.Repository
import com.example.skycast.data.source.local.LocalDataSource
import com.example.skycast.data.source.remote.RemoteDataSource
import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
import com.example.skycast.view.homeview.DayDetailsAdapter
import com.example.skycast.view.viewmodel.SharedViewModel
import com.example.skycast.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import com.example.skycast.model.pojo.fivedayforecast.List
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.celsius
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.fahrenheit
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.kelvin
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.meterPerSecond
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.milePerHour
import com.example.skycast.view.map.map
import com.example.skycast.view.setting.Setting
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest

import com.google.android.gms.location.Priority
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sharedPrefFile = "SettingPref"
    lateinit var pressureLable :String
    var tempetatureUnit: String = " "
    var windSpeedUnit:String = " "
    var language: String = " "
    var latPoint: Double = 0.0
    var lonPoint: Double = 0.0
    var windSpeedLable :String = " "
    var tempUnitLable = ""
    var arabicUnitLable=" "
    private lateinit var fusedClient: FusedLocationProviderClient
    lateinit var vmFactory: ViewModelFactory
    lateinit var viewModel: SharedViewModel

    override fun onStart() {
        super.onStart()
        viewModel.loadLanguage()
        viewModel.laodTemperatureUnit()
        viewModel.loadWindSpeedUnit()
        language = when (val result= viewModel.language.value) {
            is Result.Error -> "en"
            Result.Loading -> "en"
            is Result.Success -> result.data
        }
        LocaleUtils.setLocale(this,language)
        tempetatureUnit = when (val result = viewModel.tempeatureUnit.value) {
            is Result.Error -> celsius
            Result.Loading -> celsius
            is Result.Success -> result.data
        }
        tempUnitLable = when(tempetatureUnit){
                celsius ->getString(R.string.celis_Lable)
                fahrenheit->getString(R.string.fah_Lable)
                kelvin-> getString(R.string.kelvin_Lable)
                else -> {""}
            }
        windSpeedUnit=when (val result = viewModel.windSpeedUnit.value){
            is Result.Success-> result.data
            is Result.Error -> meterPerSecond
            Result.Loading -> milePerHour
        }

        windSpeedLable = when(windSpeedUnit){
            milePerHour-> getString(R.string.miles_hour)
            meterPerSecond->getString(R.string.meter_sec)
            else -> {"m/s"}
        }
        if (!isLocationPermissionEnabled()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1000
            )
        }
        if (!isLocationEnabled()) {
            enableLocation()
        }
        startLocationUpdate()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //  enableEdgeToEdge()
        setContentView(binding.root)
        /*creating an object from view model*/
        vmFactory = ViewModelFactory(
            Repository(
                RemoteDataSource(),
                LocalDataSource(
                    SharedPrefrenceHelper(
                        getSharedPreferences(
                            sharedPrefFile,
                            MODE_PRIVATE
                        )
                    )
                )
            )
        )
        viewModel = ViewModelProvider(this, vmFactory).get(SharedViewModel::class.java)

    }


    override fun onResume() {
        super.onResume()
        LocaleUtils.setLocale(this,language)
        val oneDayForeCastAdapter = DayDetailsAdapter(arrayListOf())
        oneDayForeCastAdapter.updateTemperatureLable(tempUnitLable)
        binding.rvWholeDayStates.apply {
            adapter = oneDayForeCastAdapter
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }

        lifecycleScope.launch {
            viewModel.fiveDaysForeCast.collect {
                when (it) {
                    is Result.Error -> Log.i("data", "fail")
                    Result.Loading -> Log.i("data", "Loading")
                    is Result.Success -> {
                        setFiveDaysForecastData(it.data, binding)
                        oneDayForeCastAdapter.updateList(
                            it.data.list.subList(
                                dayOneStart,
                                dayOneEnd
                            )
                        )
                        oneDayForeCastAdapter.notifyDataSetChanged()
                    }
                }
            }
        }




        lifecycleScope.launch {
            viewModel.currentWeatherState.collect {
                when (it) {
                    is Result.Error -> it.exception.toString()
                    is Result.Loading -> Log.i("data", "loading")
                    is Result.Success -> {
                        /*set country name */
                        setCurrentDayWeatherDetalis(it.data, binding)
                    }
                }
            }
        }
        binding.btnSetting.setOnClickListener {
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }
        binding.btnAddFavLocation.setOnClickListener{
            val intent = Intent(this , map::class.java)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun enableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun isLocationPermissionEnabled(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(5000)
                .apply { Priority.PRIORITY_HIGH_ACCURACY }.build(),
            object : LocationCallback() {
                override fun onLocationResult(location: LocationResult) {
                    super.onLocationResult(location)
                    lonPoint = location.lastLocation?.latitude!!.toDouble()
                    latPoint = location.lastLocation?.longitude!!.toDouble()
                    /*request five days forecast*/
                    viewModel.getFiveDaysForeCast(
                        lat = latPoint,
                        lon = lonPoint,
                        lan = language,
                        unit = tempetatureUnit
                    )
                    /*request the current weather data */
                    viewModel.getCurrentWeatherState(
                        lat = latPoint,
                        lon = lonPoint,
                        lan = language,
                        unit = tempetatureUnit
                    )
                }
            }, Looper.getMainLooper()
        )
    }
    /*wait for edit to select the degree descriptor */
    fun setCurrentTemperature(info: CurrentWeather, tvCurrentDegree: TextView) {

        tvCurrentDegree.text = "${info.main.temp.toInt()} ${tempUnitLable}"
    }
    /*method that will load info of current weather fore cast on home screen */
    fun setCurrentDayWeatherDetalis(info: CurrentWeather, binding: ActivityMainBinding) {
        binding.tvOneDayForecast.text = getString(R.string.dayForeCast)
        binding.tvPressure.text = getString(R.string.pressure)
        binding.tvWind.text = getString(R.string.wind)
        binding.tvCloud.text = getString(R.string.cloud)
        binding.tvSunRise.text = getString(R.string.sunrise)
        binding.tvSunSet.text = getString(R.string.sunset)
        binding.tvHumidity.text= getString(R.string.humidity)
        setCityName(info, binding.tvCityName)
        setDate(info, binding.tvDate)
        setWeatherStateImage(binding.imgvWeatherState, info.weather.get(0).icon)
        setWeatherDescription(info.weather.get(0).main, binding.tvSkyState)
        setCurrentTemperature(info, binding.tvCurrentTemp)
        binding.tvPressureValue.text = "${info.main.pressure} ${getString(R.string.pressure_unit)}"
        binding.tvHumidityValue.text = "${info.main.humidity} %"
        binding.tvWindSpeedValue.text = "${info.wind.speed} ${windSpeedLable}"
        binding.tvCloudValue.text = "${info.clouds.all} %"
        binding.tvSunRiseTime.text = "${getTimeAsHumanRedable(info.sys.sunrise.toLong())} ${getString(R.string.mor_time_unit)}"
        binding.tvSunsetTime.text = "${getTimeAsHumanRedable(info.sys.sunset.toLong()) }  ${getString(R.string.nig_time_unit)}"

    }

    /*method that will load all five days fore cast info in home screen */
    fun setFiveDaysForecastData(info: FiveDaysForeCast, binding: ActivityMainBinding) {
        /*setting the max and minimum temperature in the five day forecast */
        val dayone = getMaxAndMinTemperaturePerDay(info.list.subList(0, 7))
        val dayTwo = getMaxAndMinTemperaturePerDay(info.list.subList(8, 15))
        val daythree = getMaxAndMinTemperaturePerDay(info.list.subList(16, 23))
        val dayFour = getMaxAndMinTemperaturePerDay(info.list.subList(24, 31))
        val dayFive = getMaxAndMinTemperaturePerDay(info.list.subList(32, 39))
        binding.tvFdForecast.text=getString(R.string.dayForeCast)
        binding.tvfdToDya.text=getString(R.string.today)
        binding.tvFdTodayTemp1.text = dayone.first.toString()
        binding.tvFdTodayTemp2.text = " /${dayone.second} ${tempUnitLable} "
        binding.tvFdSecondTemp1.text = dayTwo.first.toString()
        binding.tvFdSecondTemp2.text = " /${dayTwo.second} ${tempUnitLable}"
        binding.tvFdThirdTemp1.text = daythree.first.toString()
        binding.tvFdThirdTemp2.text = " /${daythree.second} ${tempUnitLable}"
        binding.tvFdFourthTemp1.text = dayFour.first.toString()
        binding.tvFdFourthTemp2.text = " /${dayFour.second} ${tempUnitLable}"
        binding.tvFdFifthTemp1.text = dayFive.first.toString()
        binding.tvFdFifthTemp2.text = " /${dayFive.second} ${tempUnitLable}"



        binding.tvFdSecondDay.text = convertDateToHmanRedable(info.list.get(8).dt.toLong()).second
        binding.tvFdThirdDay.text = convertDateToHmanRedable(info.list.get(16).dt.toLong()).second
        binding.tvFdFourthDay.text = convertDateToHmanRedable(info.list.get(24).dt.toLong()).second
        binding.tvFdFifthDay.text = convertDateToHmanRedable(info.list.get(32).dt.toLong()).second

        setWeatherStateImage(binding.imgvFdTody, info.list.get(0).weather.get(0).icon)
        setWeatherStateImage(binding.imgvFdSecondDay, info.list.get(8).weather.get(0).icon)
        setWeatherStateImage(binding.imgvFdThirdDay, info.list.get(16).weather.get(0).icon)
        setWeatherStateImage(binding.imgvFdFourthDay, info.list.get(24).weather.get(0).icon)
        setWeatherStateImage(binding.imgvFdFifthDay, info.list.get(32).weather.get(0).icon)


        setWeatherDescription(info.list.get(0).weather[0].main, binding.tvFdTodaySkyState)
        setWeatherDescription(info.list.get(8).weather[0].main, binding.tvFdSecondSkyState)
        setWeatherDescription(info.list.get(16).weather[0].main, binding.tvFdThirdSkyState)
        setWeatherDescription(info.list.get(23).weather[0].main, binding.tvFdFouthSkyState)
        setWeatherDescription(info.list.get(32).weather[0].main, binding.tvFdFifthSkyState)

    }

}


/*method too check the minimum and mximum temperature per day , return Pair of int with m,min and max value */
fun getMaxAndMinTemperaturePerDay(dayInfo: kotlin.collections.List<List<Any?>>): Pair<Int, Int> {
    val maxTemps: MutableList<Int> = mutableListOf()
    val minTemps: MutableList<Int> = mutableListOf()
    for (i in 0 until dayInfo.size) {
        /*compare each element and get maximum value */
        maxTemps.add(i, dayInfo.get(i).main.tempMax.toInt())
        minTemps.add(i, dayInfo.get(i).main.tempMin.toInt())
    }
    /*return the max Temperature degree of the list and the minmum degree of list */
    return Pair(maxTemps.max(), minTemps.min())
}



fun setCityName(info: CurrentWeather, tvCityname: TextView) {
    tvCityname.text = " ${info.sys.country} , ${info.name}"
}

/*method to set date of current day  */
fun setDate(info: CurrentWeather, tvDate: TextView) {
    tvDate.text = convertDateToHmanRedable(info.dt).first
}

fun setWeatherDescription(info: String, tvDescription: TextView) {
    tvDescription.text = info
}

