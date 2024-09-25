package com.example.weatherviewer.ui.states

import com.example.weatherviewer.data.models.Weather

data class WeatherUiState(
    val weather: Weather? = null,
    val favouriteCity: List<Weather>? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)
