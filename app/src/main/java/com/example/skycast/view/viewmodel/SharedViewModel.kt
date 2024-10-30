package com.example.skycast.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.Result
import com.example.skycast.data.repository.IRepository
import com.example.skycast.model.pojo.current.CurrentWeather
import com.example.skycast.model.pojo.fivedayforecast.FiveDaysForeCast
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



    private val _tempeatureUnit = MutableStateFlow<Result<String>>(Result.Loading)
    val tempeatureUnit: StateFlow<Result<String>> = _tempeatureUnit


    private val _windSpeedUnit = MutableStateFlow<Result<String>>(Result.Loading)
    val windSpeedUnit : StateFlow<Result<String>> = _windSpeedUnit

    private val _language = MutableStateFlow<Result<String>>(Result.Loading)
    val language : StateFlow<Result<String>> = _language

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