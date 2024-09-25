package com.example.weatherviewer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherviewer.data.database.dao.WeatherDetailDao
import com.example.weatherviewer.data.models.Weather


@Database(entities = [Weather::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getWeatherDao(): WeatherDetailDao

}
