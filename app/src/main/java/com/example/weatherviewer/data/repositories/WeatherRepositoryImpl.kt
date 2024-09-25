package com.example.weatherviewer.data.repositories

import com.example.weatherviewer.data.models.Weather
import com.example.weatherviewer.data.sources.LocalSource
import com.example.weatherviewer.data.sources.RemoteSource
import com.example.weatherviewer.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : WeatherRepository {

    override fun findCityWeather(city: String): Flow<Result<Weather>> = flow {
        emit(Result.Loading)
        try {
            val result = remoteSource.findCityWeatherData(city)
            emit(Result.Success(result))
        } catch (exception: HttpException) {
            emit(Result.Error(exception.message.orEmpty()))
        } catch (exception: IOException) {
            emit(Result.Error("Please check your network connection and try again!"))
        }
    }.flowOn(dispatcher)
//        .catch {
//
//    }

    override suspend fun saveFavCity(favouriteCity: Weather) = localSource.saveCity(favouriteCity)

    override suspend fun getFavCities(): List<Weather> {
        return localSource.getSaveCities()
    }

    override suspend fun deleteFavCity(city: String) = localSource.deleteFavCity(city)

    override suspend fun fetchFavouriteWeatherByName(name: String): Weather =
        localSource.fetchFavouriteWeatherByName(name)


}