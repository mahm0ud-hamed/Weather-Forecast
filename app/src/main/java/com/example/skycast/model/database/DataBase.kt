package com.example.skycast.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.skycast.model.pojo.weatherEntity.WeatherEntity


@Database(entities = arrayOf(WeatherEntity::class) , version = 3)
abstract class DataBase: RoomDatabase() {
    abstract fun getWeatherDao():WeatherDao
    companion object{
        /*create a refrence in data base fro singleton*/
        @Volatile
        private var  INSTANCE : DataBase?  = null
        fun gteInstance(context: Context?):DataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context!!.applicationContext ,
                    DataBase::class.java , "Product_DataBase").fallbackToDestructiveMigration().build()
                instance
            }
        }
    }

}