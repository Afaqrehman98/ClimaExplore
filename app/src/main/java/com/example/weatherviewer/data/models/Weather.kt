package com.example.weatherviewer.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = Weather.TABLE_NAME)
data class Weather(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val temperature: Int,
    val date: String,
    val wind: Int,
    val humidity: Int,
    val feelsLike: Int,
    val uv: Int,
    val name: String,
    val isFavourite: Boolean = false
) {
    @Ignore
    var condition: ForecastResponse.Current.Condition? = null

    @Ignore
    var forecasts: List<Forecast> = emptyList()

    companion object {
        const val TABLE_NAME = "favourite_weather"

        // Helper function to create a Weather object with condition and forecasts
        fun createWithDetails(
            temperature: Int,
            date: String,
            wind: Int,
            humidity: Int,
            feelsLike: Int,
            uv: Int,
            name: String,
            isFavourite: Boolean = false,
            condition: ForecastResponse.Current.Condition? = null,
            forecasts: List<Forecast> = emptyList()
        ): Weather {
            return Weather(
                temperature = temperature,
                date = date,
                wind = wind,
                humidity = humidity,
                feelsLike = feelsLike,
                uv = uv,
                name = name,
                isFavourite = isFavourite
            ).apply {
                this.condition = condition
                this.forecasts = forecasts
            }
        }
    }
}
