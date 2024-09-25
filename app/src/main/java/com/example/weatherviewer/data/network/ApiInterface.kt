package com.example.weatherviewer.data.network

import com.example.weatherviewer.BuildConfig
import com.example.weatherviewer.data.models.ForecastResponse
import com.example.weatherviewer.utils.DEFAULT_DAYS
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {

    @GET("forecast.json")
    suspend fun findCityWeatherData(
        @Query("key") key: String = BuildConfig.API_KEY,
        @Query("q") q: String,
        @Query("days") days: Int = DEFAULT_DAYS,

        ): ForecastResponse


}