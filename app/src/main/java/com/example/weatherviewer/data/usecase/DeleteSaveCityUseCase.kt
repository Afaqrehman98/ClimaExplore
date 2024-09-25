package com.example.weatherviewer.data.usecase

import com.example.weatherviewer.data.repositories.WeatherRepository
import javax.inject.Inject

class DeleteSaveCityUseCase
    @Inject
    constructor(
        private val repositoryImpl: WeatherRepository,
    ) {
        suspend fun deleteFavCity(city: String) = repositoryImpl.deleteFavCity(city)
    }
