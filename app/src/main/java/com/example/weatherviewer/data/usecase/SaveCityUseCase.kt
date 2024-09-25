package com.example.weatherviewer.data.usecase

import com.example.weatherviewer.data.models.Weather
import com.example.weatherviewer.data.repositories.WeatherRepository
import javax.inject.Inject

class SaveCityUseCase @Inject constructor(private val repositoryImpl: WeatherRepository) {

    suspend fun saveCity(favouriteCity: Weather) = repositoryImpl.saveFavCity(favouriteCity)


}