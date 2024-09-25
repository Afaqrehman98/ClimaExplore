package com.example.weatherviewer.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherviewer.data.models.Weather

@Dao
interface WeatherDetailDao {

    /**
     * Duplicate values are replaced in the table.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavouriteWeather(favouriteCity: Weather)

    @Query("SELECT * FROM ${Weather.TABLE_NAME}")
    suspend fun fetchAllFavouriteWeathers(): List<Weather>

    @Query("DELETE FROM ${Weather.TABLE_NAME} WHERE name = :name")
    suspend fun deleteByName(name: String)


    @Query("SELECT * FROM ${Weather.TABLE_NAME} WHERE name = :name")
    suspend fun fetchFavouriteWeatherByName(name: String): Weather


}
