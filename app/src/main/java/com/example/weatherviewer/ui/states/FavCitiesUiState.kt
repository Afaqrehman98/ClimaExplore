package com.example.weatherviewer.ui.states

import com.example.weatherviewer.data.models.Weather

data class FavCitiesUiState(
    val favouriteCity: List<Weather>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)
