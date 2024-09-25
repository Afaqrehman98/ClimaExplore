package com.example.weatherviewer.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherviewer.data.models.Weather
import com.example.weatherviewer.data.usecase.DeleteSaveCityUseCase
import com.example.weatherviewer.data.usecase.GetFavouriteCityUseCase
import com.example.weatherviewer.data.usecase.GetSaveCityUseCase
import com.example.weatherviewer.data.usecase.SaveCityUseCase
import com.example.weatherviewer.data.usecase.SearchWeatherUseCase
import com.example.weatherviewer.ui.states.SearchWidgetState
import com.example.weatherviewer.ui.states.WeatherUiState
import com.example.weatherviewer.utils.DEFAULT_WEATHER_DESTINATION
import com.example.weatherviewer.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val searchWeatherUseCase: SearchWeatherUseCase,
    private val saveCityUseCase: SaveCityUseCase,
    private val getSaveCityUseCase: GetSaveCityUseCase,
    private val deleteSaveCityUseCase: DeleteSaveCityUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<WeatherUiState> =
        MutableStateFlow(WeatherUiState(isLoading = true))
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    init {
        getWeather()
    }

    fun getWeather(city: String = DEFAULT_WEATHER_DESTINATION) {
        searchWeatherUseCase.getCities(city).map { result ->
            when (result) {
                is Result.Success -> {
                    val data = getSaveCityUseCase.getSaveCities()
                    val apiData = result.data
                    val isFavorite = data.find { apiData.name == it.name }
                    _uiState.value = WeatherUiState(
                        weather = apiData,
                        favouriteCity = data,
                        isFavorite = isFavorite != null
                    )
                }

                is Result.Error -> {
                    _uiState.value = WeatherUiState(errorMessage = result.errorMessage)
                }

                Result.Loading -> {
                    _uiState.value = WeatherUiState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)

    }

    fun saveCity(favouriteCity: Weather) {
        viewModelScope.launch {
            saveCityUseCase.saveCity(favouriteCity)
            val data = getSaveCityUseCase.getSaveCities()
            _uiState.value = WeatherUiState(
                weather = _uiState.value.weather,
                favouriteCity = data,
                isFavorite = true
            )

        }

    }

    fun deleteCity(favouriteCity: String) {
        viewModelScope.launch {
            deleteSaveCityUseCase.deleteFavCity(favouriteCity)
            val data = getSaveCityUseCase.getSaveCities()
            _uiState.value = WeatherUiState(
                weather = _uiState.value.weather,
                favouriteCity = data,
                isFavorite = false
            )

        }

    }



}