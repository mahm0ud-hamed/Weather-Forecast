package com.example.skycast.view.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.skycast.databinding.ActivitySettingBinding

class Setting : AppCompatActivity() {
   lateinit var  binding :ActivitySettingBinding

   /*unit Keys */
    private  val celsius :String = "metric"
    private val kelvin :String = "standard"
    private val fahrenheit :String = "imperial"
    private val milePerHour = "imperial"
    private val meterPerSecond ="metric"

    /*language Keys */
    private val english :String ="en"
    private val arabic :String = "ar"

    /*Shared Preferences file */
    private val sharedPrefFile = "SettingPref"

    // Keys for SharedPreferences
    private val tempUnitKey = "tempUnit"
    private val languageKey = "language"
    private val windSpeedKey = "tempUnit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
        *
        *  need to handle language selected in setting
        * need to handle notification */
        loadSavedSelection()

      }


    override fun onResume() {
        super.onResume()
        selectTemperatureUnit(binding)
        selectWindSpeedUnit(binding)
        selectLanguage(binding)

    }


    private fun selectTemperatureUnit(binding: ActivitySettingBinding){
        binding.radioGroupTemperature.setOnCheckedChangeListener{ _ ,checkedId->
            val sharedPrefrences = getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
            val temperature = when(checkedId){
                binding.radioCelsius.id-> celsius
                binding.radioKelvin.id->kelvin
                binding.radioFahrenheit.id->fahrenheit
                else -> {}
            }
            saveSelection(sharedPrefrences , tempUnitKey, temperature.toString())
        }

    }

    private fun selectLanguage(binding: ActivitySettingBinding){
        binding.radioGroupLanguage.setOnCheckedChangeListener{_ , checkedId ->
            val sharedPrefrences = getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
            val language = when(checkedId){
                binding.radioEnglish.id -> english
                binding.radioArabic.id -> arabic
                else->{}
            }
            saveSelection(sharedPrefrences , languageKey , language.toString())

        }
    }

    private fun selectWindSpeedUnit(binding: ActivitySettingBinding){
        binding.radioGroupWind.setOnCheckedChangeListener{ _ , checkedId ->
            val sharedPrefrences = getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
            val windSpeed =when(checkedId){
                binding.radioMilesPerHour.id -> milePerHour
                binding.radioMeterPerSecond.id -> meterPerSecond
                else->{}
            }
            saveSelection(sharedPrefrences , windSpeedKey , windSpeed.toString())

        }
    }

    private fun saveSelection(sharedPref :SharedPreferences , key :String , value :String){
        sharedPref.edit().putString(key ,value ).commit()
    }

    private fun loadSavedSelection(){
        val sharedPrefrence = getSharedPreferences(sharedPrefFile , MODE_PRIVATE)
        /*read the last selection unit of temperature */
        when(sharedPrefrence.getString(tempUnitKey , kelvin)){
            celsius->binding.radioGroupTemperature.check(binding.radioCelsius.id)
            kelvin -> binding.radioGroupTemperature.check(binding.radioKelvin.id)
            fahrenheit->binding.radioGroupTemperature.check(binding.radioFahrenheit.id)
        }

        /*read the last selection unit of wind speed */
        when(sharedPrefrence.getString(windSpeedKey , milePerHour)){
            milePerHour -> binding.radioGroupWind.check(binding.radioMilesPerHour.id)
            meterPerSecond -> binding.radioGroupWind.check(binding.radioMeterPerSecond.id)
        }
        /*read the last selection unit in language */
        when(sharedPrefrence.getString(languageKey, english)){
            english->binding.radioGroupLanguage.check(binding.radioEnglish.id)
            arabic->binding.radioGroupLanguage.check(binding.radioArabic.id)
            else ->{}
        }
    }
}

