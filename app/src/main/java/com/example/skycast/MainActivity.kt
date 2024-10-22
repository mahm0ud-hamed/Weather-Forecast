package com.example.skycast

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skycast.data.Result
import com.example.skycast.databinding.ActivityMainBinding
import com.example.skycast.data.repository.Repository
import com.example.skycast.data.source.remote.RemoteDataSource
import com.example.skycast.model.commonpojo.Weather
import com.example.skycast.view.homeview.HomeViewModel
import com.example.skycast.view.homeview.HomeVmFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        /*creating an object from view model*/
        var vmFactory = HomeVmFactory(Repository(RemoteDataSource()))
        var viewModel = ViewModelProvider(this , vmFactory).get(HomeViewModel::class.java)


        viewModel.getCurrentWeatherState(lat = 30.4182 , lon = 30.5747 ,lan="ar", unit = "imperial" )

        lifecycleScope.launch {
            viewModel.currentWeatherState.collect{
                when(it){
                    is Result.Error -> it.exception.toString()
                    is Result.Loading -> Log.i("data" , "loading")
                    is Result.Success -> it.data.collect{
                        Log.i("data",it.weather.get(0).description)
                        Log.i("data",it.wind.speed.toString())
                        Log.i("data",it.main.toString())
                    }

                    else -> {
                        println("حقك عليا , كل مر سيمر  ")
                    }
                }
            }
        }
    }
}