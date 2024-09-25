package com.example.weatherviewer.data.repositories


import com.example.weatherviewer.data.models.Weather
import com.example.weatherviewer.utils.Result
import kotlinx.coroutines.flow.Flow


interface WeatherRepository {
    fun findCityWeather(city: String): Flow<Result<Weather>>
    suspend fun saveFavCity(favouriteCity: Weather)
    suspend fun getFavCities(): List<Weather>
    suspend fun deleteFavCity(city: String)
    suspend fun fetchFavouriteWeatherByName(name: String): Weather

}