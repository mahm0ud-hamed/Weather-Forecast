package com.example.skycast

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.skycast.network.RetrofitHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        /*testing returened data over network */

        /*create an instance from retrofit helper  */
        lifecycleScope.launch {
            var response = RetrofitHelper.service.getWatherSatateByLongAndLat()
            Log.i("response" , response.list.get(0).main.toString())
            Log.i("response" , response.list.get(0).weather.toString())
            Log.i("response" , response.list.get(0).dtTxt)
            Log.i("response" , response.list.get(0).wind.toString())
            Log.i("response" , response.list.get(0).clouds.toString())
            Log.i("response" , response.list.get(0).pop.toString())
            Log.i("response" , response.list.get(0).visibility.toString())
            Log.i("response" , response.city.toString())
            Log.i("response" , response.city.coord.toString())
            Log.i("response" , response.cod.toString())
            Log.i("Message" , response.message.toString())

        }
    }
}