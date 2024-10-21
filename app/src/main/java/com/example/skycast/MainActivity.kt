package com.example.skycast

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.skycast.model.repository.Repository
import com.example.skycast.network.RemoteDataSource
import com.example.skycast.network.RetrofitHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        /*testing returened data over network */
        var repo = Repository(RemoteDataSource())
        /*create an instance from retrofit helper  */
        lifecycleScope.launch {
//            var response = RetrofitHelper.service.getFiveDayForeCast(30.364361 ,30.506901 )
//            var current = RetrofitHelper.service.getCurrentWeatherData(30.364361 ,30.506901 )
            var response=  repo.getRemoteFiveDaysForeCast(30.364361 ,30.506901 )
            var current=  repo.getRemoteCurrentWeatherState(30.364361 ,30.506901 )
            Log.i("current" , ",ad")

            current.collect{
                it.main.toString()
            }
            response.collect{

                Log.i("response" , it.city.toString())
                Log.i("icon" , it.list.get(39).weather.get(0).icon)

            }







        }
    }
}