package com.example.skycast.view.homeview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.Result
import com.example.skycast.data.repository.IRepository
import com.example.skycast.model.current.CurrentWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class HomeViewModel(val repository: IRepository):ViewModel(){
    /*mutable state flow to store data come from repository*/
    private var _currentWeatherState = MutableStateFlow<Result<Flow<CurrentWeather>>>(Result.Loading)
   /*state flow to use in view to show data come from repository */
    val  currentWeatherState : StateFlow<Result<Flow<CurrentWeather>>> = _currentWeatherState

    fun getCurrentWeatherState(lat :Double , lon :Double, lan :String , unit: String){
        viewModelScope.launch {
            _currentWeatherState.value=repository.getRemoteCurrentWeatherState(lat , lon, lan , unit)
        }
    }


}