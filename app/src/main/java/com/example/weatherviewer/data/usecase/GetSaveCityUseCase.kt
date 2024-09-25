package com.example.weatherviewer.data.usecase

import com.example.weatherviewer.data.repositories.WeatherRepository
import javax.inject.Inject

class GetSaveCityUseCase @Inject constructor(private val repositoryImpl: WeatherRepository) {
     suspend fun getSaveCities() = repositoryImpl.getFavCities()
}