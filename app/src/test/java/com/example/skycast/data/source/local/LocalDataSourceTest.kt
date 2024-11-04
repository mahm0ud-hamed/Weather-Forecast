package com.example.skycast.data.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.model.database.AlarmDao
import com.example.skycast.model.database.DataBase
import org.jetbrains.annotations.ApiStatus.Experimental
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith


@ExperimentalStdlibApi
@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest{

    lateinit var  dataBase: DataBase
    lateinit var dao: AlarmDao
    lateinit var localDataSrce : LocalDataSource

    /* wee need to make test run sequentially  even if there is a threads  */
}