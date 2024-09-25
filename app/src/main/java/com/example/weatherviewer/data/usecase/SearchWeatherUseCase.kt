package com.example.weatherviewer.data.usecase

import com.example.weatherviewer.data.models.Weather
import com.example.weatherviewer.data.repositories.WeatherRepository
import com.example.weatherviewer.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchWeatherUseCase @Inject constructor(private val repositoryImpl: WeatherRepository) {
    fun getCities(cityName: String): Flow<Result<Weather>> {
        return repositoryImpl.findCityWeather(cityName)
    }
}