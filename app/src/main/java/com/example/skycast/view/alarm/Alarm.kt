package com.example.skycast.view.alarm

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.MainActivity
import com.example.skycast.data.Result
import com.example.skycast.data.repository.Repository
import com.example.skycast.data.source.local.LocalDataSource
import com.example.skycast.data.source.remote.RemoteDataSource
import com.example.skycast.databinding.ActivityAlarmBinding
import com.example.skycast.model.database.DataBase
import com.example.skycast.model.pojo.alarmentity.WeatherAlarm
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper
import com.example.skycast.view.favourite.FavouriteAdapter
import com.example.skycast.view.viewmodel.SharedViewModel
import com.example.skycast.view.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Calendar

class Alarm : AppCompatActivity() {
    lateinit var binding: ActivityAlarmBinding
    lateinit var vmFactory: ViewModelFactory
    lateinit var viewModel: SharedViewModel
    private val sharedPrefFile = "SettingPref"
    lateinit var alarmAdapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmAdapter = AlarmAdapter(arrayListOf()){
            deleteAlarm(it)
            viewModel.getAllAlarms()

        }
        binding.rvAlarms.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(this@Alarm).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
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
        viewModel.getAllAlarms()

        lifecycleScope.launch {
            viewModel.alarms.collect{
                when(it){
                    is Result.Error -> {}
                    Result.Loading ->{}
                    is Result.Success ->
                        if(it.data.isNotEmpty()){
                            scheduleAlarms(it.data)
                            alarmAdapter.updateList(it.data)
                            alarmAdapter.notifyDataSetChanged()
                        }else{
                            alarmAdapter.updateList(arrayListOf())
                        }
                }

            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.btnAddAlarm.setOnClickListener {
            showDateTimePicker()
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            showTimePicker(calendar)
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    private fun showTimePicker(calendar: Calendar) {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            // Convert to milliseconds

            val triggerAtMillis = calendar.timeInMillis
            if (triggerAtMillis > System.currentTimeMillis()) {
                viewModel.saveAlarm(triggerAtMillis, "Weather Info Here")
                Toast.makeText(this , "Saved", Toast.LENGTH_SHORT).show()// Save only if in the future
            } else {
               Toast.makeText(this , "Alarm set for past time, not saving", Toast.LENGTH_SHORT).show()
            }

        }

        TimePickerDialog(
            this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    // Private method for scheduling alarms
    private fun scheduleAlarms(alarms: List<WeatherAlarm>) {
        val currentTime = System.currentTimeMillis()
        alarms.forEach { alarm ->
            if (alarm.triggerAtMillis > currentTime) {
                AlarmManagerHelper.setAlarm(this, alarm.triggerAtMillis, alarm.weatherInfo, alarm.id.toInt())
            }
        }
    }


    private fun deleteAlarm(alarm: WeatherAlarm){
        viewModel.deleteAlarm(alarm.id)
        AlarmManagerHelper.cancelAlarm(this , alarm.id.toInt())
        viewModel.getAllAlarms()
    }

}



