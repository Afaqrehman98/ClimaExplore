package com.example.weatherviewer.data.sources

import com.example.weatherviewer.data.models.Weather
import com.example.weatherviewer.data.models.toWeather
import com.example.weatherviewer.data.network.ApiInterface
import javax.inject.Inject

class RemoteSource @Inject constructor(private val api: ApiInterface) {

    suspend fun findCityWeatherData(city: String): Weather {
        return api.findCityWeatherData(q = city).toWeather()
    }
}