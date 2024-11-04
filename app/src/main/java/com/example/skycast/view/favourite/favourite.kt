package com.example.skycast.view.favourite

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.example.skycast.Util.isNetworkAvailable
import com.example.skycast.data.Result
import com.example.skycast.data.repository.Repository
import com.example.skycast.data.source.local.LocalDataSource
import com.example.skycast.data.source.remote.RemoteDataSource
import com.example.skycast.databinding.ActivityFavouriteBinding
import com.example.skycast.model.database.DataBase
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper
import com.example.skycast.view.map.map
import com.example.skycast.view.viewmodel.SharedViewModel
import com.example.skycast.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch


class favourite : AppCompatActivity() {
    lateinit var binding: ActivityFavouriteBinding
    lateinit var favouriteAdapter: FavouriteAdapter

    lateinit var vmFactory: ViewModelFactory
    lateinit var viewModel: SharedViewModel
    private val sharedPrefFile = "SettingPref"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
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

        // Initialize RecyclerView and Adapter
        favouriteAdapter = FavouriteAdapter(arrayListOf(),
            { location ->
                viewModel.deleteSavedLcoation(location) // Delete the location
            },
            { location ->
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("cityName", location.cityName)
                    putExtra("fromFav", true)
                }
                startActivity(intent)
            }
        )

        binding.rvFavs.apply {
            adapter = favouriteAdapter
            layoutManager = LinearLayoutManager(this@favourite).apply {
                orientation = RecyclerView.VERTICAL
            }
        }

        backButtonAction()
        addNewFAvourite()
    }


    override fun onStart() {
        super.onStart()
        getCurrentFavouriteList()
    }




    private fun getCurrentFavouriteList(){

        viewModel.getAllSavedLocations()
        lifecycleScope.launch {
            viewModel.savedLocations.collect {
                when (it) {
                    is Result.Error -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        favouriteAdapter.updateList(it.data)
                    }
                }
            }
        }
    }
    private fun backButtonAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    private fun addNewFAvourite() {

        binding.btnMaps.setOnClickListener {
            if (isNetworkAvailable(this)) {
                val intent = Intent(this, map::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.no_Connection), Toast.LENGTH_SHORT).show()
            }

        }

    }

}