package com.example.skycast.view.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat.WearableExtender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.Result
import com.example.skycast.data.repository.IRepository
import com.example.skycast.model.pojo.alarmentity.WeatherAlarm
import com.example.skycast.model.pojo.commonpojo.Weather
import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.City
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedViewModel(val repository: IRepository):ViewModel(){
    /*mutable state flow to store data come from repository*/
    private var _currentWeatherState = MutableStateFlow<Result<CurrentWeather>>(Result.Loading)
   /*state flow to use in view to show data come from repository */
    val  currentWeatherState : StateFlow<Result<CurrentWeather>> = _currentWeatherState

    /*mutable state flow to store the five day fore cast */
    private var _fiveDaysForeCast = MutableStateFlow<Result<FiveDaysForeCast>>(Result.Loading)
   /* state flow to use in view to show data come from repository */
    var fiveDaysForeCast :StateFlow<Result<FiveDaysForeCast>> =_fiveDaysForeCast


    private val _tempeatureUnit = MutableStateFlow<Result<String>>(Result.Loading)
    val tempeatureUnit: StateFlow<Result<String>> = _tempeatureUnit


    private val _windSpeedUnit = MutableStateFlow<Result<String>>(Result.Loading)
    val windSpeedUnit : StateFlow<Result<String>> = _windSpeedUnit

    private val _language = MutableStateFlow<Result<String>>(Result.Loading)
    val language : StateFlow<Result<String>> = _language

    private val _latLognPoints = MutableStateFlow<Result<Pair<Double,Double>>>(Result.Loading)
    val latLongPoints :StateFlow<Result<Pair<Double,Double>>>  = _latLognPoints


    private val _SavedLocations = MutableStateFlow<Result<List<WeatherEntity>>>(Result.Loading)
    val savedLocations :StateFlow<Result<List<WeatherEntity>>> = _SavedLocations

    private val _savedCity = MutableStateFlow<Result<WeatherEntity>>(Result.Loading)
    val savedCity :MutableStateFlow<Result<WeatherEntity>> = _savedCity

    private val _alarms = MutableStateFlow<Result<List<WeatherAlarm>>>(Result.Loading)
    val alarms : StateFlow<Result<List<WeatherAlarm>>> =_alarms

    private val _LocationDetection = MutableStateFlow<String>("gps")
    val locationDetection :StateFlow<String> = _LocationDetection

    fun getLocationDetection(){
        viewModelScope.launch {
            val location =  repository.loadLocationDetection()
            _LocationDetection.value = location
        }

    }

    fun getAllAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms().collect{
                _alarms.value= it
            }
        }

    }

    fun deleteAlarm(id :Long){
        viewModelScope.launch {
            repository.deleteAlams(id)
            getAllAlarms()
        }
    }

    fun saveAlarm(triggerAtMillis: Long, weatherInfo: String){
        val alarm = WeatherAlarm(triggerAtMillis = triggerAtMillis, weatherInfo = weatherInfo)
        viewModelScope.launch {
            repository.savaAlarm(alarm)
            getAllAlarms()
        }
    }


    fun getAllSavedLocations(){
        viewModelScope.launch {
            repository.getAllSavedLocations().collect{
                _SavedLocations.value = it
            }
        }
    }
    fun saveLocation(weatherEntity: WeatherEntity){
        viewModelScope.launch {
            repository.saveLocation(weatherEntity)
            getAllSavedLocations()
        }
    }

    fun deleteSavedLcoation(weatherEntity: WeatherEntity){
        viewModelScope.launch {
            repository.deleteLocation(weatherEntity)
            getAllSavedLocations()
        }
    }

    fun getSavedLocationByCityName(cityName : String){
        viewModelScope.launch {
            repository.getSavedLocationByCityName(cityName).collect{
                _savedCity.value = it
            }
        }
        Log.i("vmCity", cityName)
    }
    fun getSavedLatLongPointOfLocation(){
        viewModelScope.launch {
            repository.loadLatAndLongOfLocation().collect{
                _latLognPoints.value = it

            }
        }
    }

    fun getFiveDaysForeCast(lat :Double , lon :Double, lan :String , unit: String){
        viewModelScope.launch {
            repository.getRemoteFiveDaysForeCast(lat , lon ,lan , unit).collect{
                _fiveDaysForeCast .value= it
            }
        }
    }
    fun getCurrentWeatherState(lat :Double , lon :Double, lan :String , unit: String){
        viewModelScope.launch {
            repository.getRemoteCurrentWeatherState(lat , lon, lan , unit).collect{
                _currentWeatherState.value= it
            }
        }
    }




    fun laodTemperatureUnit() {
        viewModelScope.launch {
            repository.loadTemperatureUnit().collect{
                _tempeatureUnit.value = it
            }
        }
    }

    fun loadWindSpeedUnit(){
        viewModelScope.launch {
            repository.loadWindSpeedUnit().collect{
                _windSpeedUnit.value = it
            }
        }
    }

    fun loadLanguage(){
        viewModelScope.launch {
            repository.loadLanguage().collect{
                _language.value = it
            }
        }
    }

    fun saveSelection(key :String , value :String){
        viewModelScope.launch {
            repository.saveSelection(key, value)
        }
    }



}