package com.example.skycast

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
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
import com.example.skycast.model.database.DataBase
import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
import com.example.skycast.view.homeview.DayDetailsAdapter
import com.example.skycast.view.viewmodel.SharedViewModel
import com.example.skycast.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import com.example.skycast.model.pojo.fivedayforecast.List
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.celsius
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.fahrenheit
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.gps
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.kelvin
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.maps
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.meterPerSecond
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.milePerHour
import com.example.skycast.view.alarm.Alarm
import com.example.skycast.view.favourite.favourite
import com.example.skycast.view.map.map
import com.example.skycast.view.setting.Setting
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest

import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val sharedPrefFile = "SettingPref"
    var tempetatureUnit: String = " "
    var windSpeedUnit:String = " "
    var language: String = " "
    var latPoint: Double = 0.0
    var lonPoint: Double = 0.0
    var windSpeedLable :String = " "
    var tempUnitLable = ""
    var locationDetection = gps
    var isFromFav:Boolean = false
    private var isViewOnly: Boolean = false
    private var isHome: Boolean = false
    private lateinit var fusedClient: FusedLocationProviderClient
    lateinit var vmFactory: ViewModelFactory
    lateinit var currentWeather :CurrentWeather
    lateinit var  fiveDaysForeCast :FiveDaysForeCast
    lateinit var viewModel: SharedViewModel
    lateinit var oneDayForeCastAdapter : DayDetailsAdapter
    lateinit var cityName : String


    override fun onStart() {
        super.onStart()
        checkLanguage()
        colloectInfoFromIntent()
        loadLocationDetection()
        /*handeling open of Home form the application launcher */
        viewModel.getSavedLatLongPointOfLocation()
        setHome()
        setViewrPage()
        getTodayWeatherState()
        getFiveDaysWeatherSate()
        setUnitsAndLables()
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
      // startLocationUpdate()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
                    ), DataBase.gteInstance(this).getWeatherDao(),
                        DataBase.gteInstance(this).getAlarmDao()
                )
            )
        )
        viewModel = ViewModelProvider(this, vmFactory).get(SharedViewModel::class.java)

    }


    override fun onResume() {
        super.onResume()
        setAdapter()
        setFiveDaysStetonUI()
       setCurrentStateOnUI()
        buttonsListners()
    }


    private  fun loadLocationFromDataBase(){
        viewModel.getSavedLocationByCityName(cityName)
        lifecycleScope.launch {
            viewModel.savedCity.collect{
                when(it){
                    is Result.Error ->{}
                    Result.Loading -> {}
                    is Result.Success -> {
                        val currentWeather = CurrentWeather(it.data.coord ,
                            it.data.weather,
                            it.data.base,
                            it.data.main ,
                            it.data.visibility,
                            it.data.wind,
                            it.data.clouds ,
                            it.data.dt ,
                            it.data.sys ,
                            it.data.timezone,
                            it.data.id ,
                            it.data.cityName,
                            it.data.cod
                        )
                        setCurrentDayWeatherDetalis(currentWeather)
                        setFiveDaysForecastData(it.data.list)
                        Log.i("from fav ", it.data.cityName)
                    }
                }
            }
        }

    }
    private fun loadLocationDetection(){
        viewModel.getLocationDetection()
        lifecycleScope.launch {
            viewModel.locationDetection.collect{
                locationDetection = it
            }
        }

        Log.i("location" , locationDetection)
    }
    /*check if location is enabled or not */
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /*enable location is enabled */
    private fun enableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    /*check for location permission*/
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
                    getFiveDaysWeatherSate()
                    /*request the current weather data */
                    getTodayWeatherState()
                }
            }, Looper.getMainLooper()
        )
    }
    /*wait for edit to select the degree descriptor */
    fun setCurrentTemperature(info: CurrentWeather, tvCurrentDegree: TextView) {

        tvCurrentDegree.text = "${info.main.temp.toInt()} ${tempUnitLable}"
    }
    /*method that will load info of current weather fore cast on home screen */
    fun setCurrentDayWeatherDetalis(info: CurrentWeather) {
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
    fun setFiveDaysForecastData(info: kotlin.collections.List<List>) {
        /*setting the max and minimum temperature in the five day forecast */
        val dayone = getMaxAndMinTemperaturePerDay(info.subList(0, 7))
        val dayTwo = getMaxAndMinTemperaturePerDay(info.subList(8, 15))
        val daythree = getMaxAndMinTemperaturePerDay(info.subList(16, 23))
        val dayFour = getMaxAndMinTemperaturePerDay(info.subList(24, 31))
        val dayFive = getMaxAndMinTemperaturePerDay(info.subList(32, 39))
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
        binding.tvFdFifthTemp2.text = " /${dayFive.second} $tempUnitLable"



        binding.tvFdSecondDay.text = convertDateToHmanRedable(info[8].dt.toLong()).second
        binding.tvFdThirdDay.text = convertDateToHmanRedable(info[16].dt.toLong()).second
        binding.tvFdFourthDay.text = convertDateToHmanRedable(info[24].dt.toLong()).second
        binding.tvFdFifthDay.text = convertDateToHmanRedable(info[32].dt.toLong()).second

        setWeatherStateImage(binding.imgvFdTody, info[0].weather[0].icon)
        setWeatherStateImage(binding.imgvFdSecondDay, info[8].weather[0].icon)
        setWeatherStateImage(binding.imgvFdThirdDay, info[16].weather.get(0).icon)
        setWeatherStateImage(binding.imgvFdFourthDay, info[24].weather.get(0).icon)
        setWeatherStateImage(binding.imgvFdFifthDay, info[32].weather.get(0).icon)


        setWeatherDescription(info[0].weather[0].main, binding.tvFdTodaySkyState)
        setWeatherDescription(info[8].weather[0].main, binding.tvFdSecondSkyState)
        setWeatherDescription(info[16].weather[0].main, binding.tvFdThirdSkyState)
        setWeatherDescription(info[23].weather[0].main, binding.tvFdFouthSkyState)
        setWeatherDescription(info[32].weather[0].main, binding.tvFdFifthSkyState)

    }

    private fun setViewrPage(){

        if(isViewOnly){
            /*ste visibility of some view component gone */
            binding.btnAddFavLocation.visibility = View.GONE
            binding.btnFavourite.visibility = View.GONE
            binding.btnNotfication.visibility= View.GONE
            binding.btnSave.visibility= View.VISIBLE

        }
    }

    private fun setHome() {

        if (!isFromFav && (locationDetection == maps)) {
            loadLatituedAndLongtiude()
            Log.i("hello" , "hello from maps")

        }
        else if(!isFromFav && (locationDetection == gps)){
            startLocationUpdate()
            Log.i("hello" , "hello from gps")

        }
        else if(isFromFav){
            loadLocationFromDataBase()
            Log.i("hello" , "hello")
        }
    }

    private fun loadLatituedAndLongtiude(){
        lifecycleScope.launch {
            viewModel.latLongPoints.collect {
                when (it) {
                    is Result.Success -> {
                        latPoint = it.data.first
                        lonPoint = it.data.second
                    }

                    is Result.Error -> {
                        latPoint = 0.0; lonPoint = 0.0
                    }

                    Result.Loading -> {}
                }
            }
        }
    }
    private fun colloectInfoFromIntent(){
        /*collect lat and long points that passed with intent */
        latPoint = intent.getDoubleExtra("LATITUDE" , 0.0)
        lonPoint = intent.getDoubleExtra("LONGTIUDE" , 0.0)
        /*chehck if come to just view the passed location*/
        isViewOnly = intent.getBooleanExtra("ToView" , false)
        /*check if come to seet the passed location as the home location */
        isHome= intent.getBooleanExtra("ToHome" , false)
        isFromFav = intent.getBooleanExtra("fromFav", false)
        cityName = intent.getStringExtra("cityName").toString()
        Log.i("cityName" , cityName)

    }

    private fun checkLanguage () {
        viewModel.loadLanguage()
        language = when (val result= viewModel.language.value) {
            is Result.Error -> "en"
            Result.Loading -> "en"
            is Result.Success -> result.data
        }
        LocaleUtils.setLocale(this,language)
    }
    private fun getTodayWeatherState() {
        viewModel.getCurrentWeatherState(
            lat = latPoint,
            lon = lonPoint,
            lan = language,
            unit = tempetatureUnit
        )
    }

    private fun getFiveDaysWeatherSate(){
        viewModel.getFiveDaysForeCast(
            lat = latPoint,
            lon = lonPoint,
            lan = language,
            unit = tempetatureUnit
        )
    }

    private fun setUnitsAndLables(){

        viewModel.laodTemperatureUnit()
        viewModel.loadWindSpeedUnit()
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
    }
    private fun setCurrentStateOnUI(){
        lifecycleScope.launch {
            viewModel.currentWeatherState.collect {
                when (it) {
                    is Result.Error -> it.exception.toString()
                    is Result.Loading -> Log.i("data", "loading")
                    is Result.Success -> {
                        /*set country name */
                        setCurrentDayWeatherDetalis(it.data)
                        currentWeather = it.data
                    }
                }
            }
        }
    }
    private fun setFiveDaysStetonUI(){
        lifecycleScope.launch {
            viewModel.fiveDaysForeCast.collect {
                when (it) {
                    is Result.Error -> Log.i("data", "fail")
                    Result.Loading -> Log.i("data", "Loading")
                    is Result.Success -> {
                        setFiveDaysForecastData(it.data.list)
                        oneDayForeCastAdapter.updateList(
                            it.data.list.subList(
                                dayOneStart,
                                dayOneEnd
                            )
                        )
                        oneDayForeCastAdapter.notifyDataSetChanged()
                        fiveDaysForeCast = it.data
                    }
                }
            }
        }

    }

    private fun setAdapter(){
        LocaleUtils.setLocale(this,language)
        oneDayForeCastAdapter = DayDetailsAdapter(arrayListOf())
        oneDayForeCastAdapter.updateTemperatureLable(tempUnitLable)
        binding.rvWholeDayStates.apply {
            adapter = oneDayForeCastAdapter
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
    }

    private fun buttonsListners(){
        binding.btnSetting.setOnClickListener {
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }
        binding.btnAddFavLocation.setOnClickListener{
            val intent = Intent(this , map::class.java)
            startActivity(intent)
        }

        binding.btnFavourite.setOnClickListener {
            val intent = Intent(this , favourite::class.java)
            startActivity(intent)
        }
        binding.btnNotfication.setOnClickListener {
            startActivity(Intent(this , Alarm::class.java))
        }
        binding.btnSave.setOnClickListener{
            val weatherEntity= WeatherEntity(
                currentWeather.name ,
                currentWeather.coord,
                currentWeather.weather ,
                currentWeather.base ,
                currentWeather.main ,
                currentWeather.visibility,
                currentWeather.wind ,
                currentWeather.clouds ,
                currentWeather.dt ,
                currentWeather.sys ,
                currentWeather.timezone ,
                currentWeather.id,
                currentWeather.cod,
                fiveDaysForeCast.list
            )
            viewModel.saveLocation(weatherEntity)
        }
    }
}


/*method too check the minimum and mximum temperature per day , return Pair of int with m,min and max value */
fun getMaxAndMinTemperaturePerDay(dayInfo: kotlin.collections.List<List>): Pair<Int, Int> {
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


