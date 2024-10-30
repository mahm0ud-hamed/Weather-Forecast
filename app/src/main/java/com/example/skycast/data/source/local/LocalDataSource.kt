package com.example.skycast.data.source.local

import com.example.skycast.data.Result
import com.example.skycast.model.sharedprefrence.ISharedPrefrenceHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalDataSource(val sharedPrefHelper: ISharedPrefrenceHelper) : ILocalDataSource {
    override suspend fun saveSelectionInSharedPref(key: String, value: String) {
        sharedPrefHelper.saveSelection(key, value)
    }

    override suspend fun loadSavedTemperatureUnit(): Flow<Result<String>> = flow {
        try {
            val tempUnit = sharedPrefHelper.laodTemperatureUnit()
            emit(Result.Success(tempUnit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun loadSavedLanguage(): Flow<Result<String>> = flow {
        try {
            val language = sharedPrefHelper.loadLanguage()
            emit(Result.Success(language))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun loadWindSpeedUnit(): Flow<Result<String>> = flow {
        try {
            val seedUnit = sharedPrefHelper.loadWindSpeedUnit()
            emit(Result.Success(seedUnit))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}