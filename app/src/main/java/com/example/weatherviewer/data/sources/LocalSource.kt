package com.example.weatherviewer.data.sources

import com.example.weatherviewer.data.database.dao.WeatherDetailDao
import com.example.weatherviewer.data.models.Weather
import javax.inject.Inject

class LocalSource @Inject constructor(private val weatherDetailDao: WeatherDetailDao) {

    suspend fun saveCity(favouriteCity: Weather) =
        weatherDetailDao.addFavouriteWeather(favouriteCity)

    suspend fun getSaveCities(): List<Weather> {
        return weatherDetailDao.fetchAllFavouriteWeathers()
    }

    suspend fun deleteFavCity(city: String) = weatherDetailDao.deleteByName(city)

    suspend fun fetchFavouriteWeatherByName(name: String) =
        weatherDetailDao.fetchFavouriteWeatherByName(name)
}