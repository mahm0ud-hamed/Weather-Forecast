package com.example.skycast.view.setting

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skycast.Util.LocaleUtils
import com.example.skycast.data.Result
import com.example.skycast.data.repository.Repository
import com.example.skycast.data.source.local.LocalDataSource
import com.example.skycast.data.source.remote.RemoteDataSource
import com.example.skycast.databinding.ActivitySettingBinding
import com.example.skycast.model.database.DataBase
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.arabic
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.celsius
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.english
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.fahrenheit
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.kelvin
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.languageKey
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.meterPerSecond
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.milePerHour
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.tempUnitKey
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.windSpeedKey
import com.example.skycast.view.viewmodel.SharedViewModel
import com.example.skycast.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale


class Setting : AppCompatActivity() {
    lateinit var vmFacroty: ViewModelFactory
    lateinit var setingViewModel: SharedViewModel
    lateinit var binding: ActivitySettingBinding
    private val sharedPrefFile = "SettingPref"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*creating obejcet from view model factory */
        vmFacroty =ViewModelFactory(
            Repository(
                RemoteDataSource(),
                LocalDataSource(
                    SharedPrefrenceHelper(
                        getSharedPreferences(
                            sharedPrefFile,
                            MODE_PRIVATE
                        )
                    ), DataBase.gteInstance(this).getWeatherDao()
                )
            )
        )
        /*creating an object from setting view model*/
        setingViewModel = ViewModelProvider(this, vmFacroty).get(SharedViewModel::class.java)
        setingViewModel.laodTemperatureUnit()
        setingViewModel.loadLanguage()
        setingViewModel.loadWindSpeedUnit()
    }


    override fun onResume() {
        super.onResume()
        selectTemperatureUnit(binding)
        selectWindSpeedUnit(binding)
        selectLanguage(binding)
        lifecycleScope.launch {
            setingViewModel.tempeatureUnit.collect {
                when(it){
                    is Result.Error -> { /*do nothing*/}
                    is Result.Loading -> {/*do nothing*/}
                    is Result.Success -> {
                       when(it.data){
                           celsius->binding.radioGroupTemperature.check(binding.radioCelsius.id)
                           fahrenheit->binding.radioGroupTemperature.check((binding.radioFahrenheit.id))
                           kelvin->binding.radioGroupTemperature.check((binding.radioKelvin.id))
                       }
                    }
                }
            }
        }
        lifecycleScope.launch {
            setingViewModel.windSpeedUnit.collect{
                when(it){
                    is Result.Error -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        when(it.data){
                            milePerHour -> binding.radioGroupWind.check(binding.radioMilesPerHour.id)
                            meterPerSecond-> binding.radioGroupWind.check(binding.radioMeterPerSecond.id)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            setingViewModel.language.collect{
                when(it){
                    is Result.Error -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        when(it.data){
                            arabic->binding.radioGroupLanguage.check(binding.radioArabic.id)
                            english->binding.radioGroupLanguage.check((binding.radioEnglish.id))
                        }
                    }
                }
            }
        }

        backButtonAction()
    }


    private fun selectTemperatureUnit(binding: ActivitySettingBinding) {
        binding.radioGroupTemperature.setOnCheckedChangeListener { _, checkedId ->
            val temperature = when (checkedId) {
                binding.radioCelsius.id -> celsius
                binding.radioKelvin.id -> kelvin
                binding.radioFahrenheit.id -> fahrenheit
                else -> {}
            }
            setingViewModel.saveSelection(tempUnitKey, temperature.toString())
        }

    }

    private fun selectLanguage(binding: ActivitySettingBinding) {
        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val language = when (checkedId) {
                binding.radioEnglish.id -> english
                binding.radioArabic.id -> arabic
                else -> {}
            }
            setingViewModel.saveSelection(languageKey, language.toString())
            Log.i("lang" , language.toString())
            LocaleUtils.setLocale(this , language.toString())
            this.recreate()
        }
    }

    private fun selectWindSpeedUnit(binding: ActivitySettingBinding) {
        binding.radioGroupWind.setOnCheckedChangeListener { _, checkedId ->
            val windSpeed = when (checkedId) {
                binding.radioMilesPerHour.id -> milePerHour
                binding.radioMeterPerSecond.id -> meterPerSecond
                else -> {}
            }
            setingViewModel.saveSelection(windSpeedKey, windSpeed.toString())

        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        // Update the configuration for the current resources
       this .resources.updateConfiguration(config, this.resources.displayMetrics)

        val laoutDirection = if(locale.language == arabic){
            View.LAYOUT_DIRECTION_RTL
        }else {
            View.LAYOUT_DIRECTION_LTR
        }
        this.window.decorView.layoutDirection =laoutDirection
        // Restart the activity to apply the language change
        this.recreate()
    }

    private fun backButtonAction(){
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

