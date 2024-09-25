package com.example.weatherviewer.di


import android.content.Context
import androidx.room.Room
import com.example.weatherviewer.data.database.AppDatabase
import com.example.weatherviewer.data.database.dao.WeatherDetailDao
import com.example.weatherviewer.data.interceptors.NetworkConnectionInterceptor
import com.example.weatherviewer.data.network.ApiInterface
import com.example.weatherviewer.data.repositories.WeatherRepository
import com.example.weatherviewer.data.repositories.WeatherRepositoryImpl
import com.example.weatherviewer.data.sources.LocalSource
import com.example.weatherviewer.data.sources.RemoteSource
import com.example.weatherviewer.data.usecase.DeleteSaveCityUseCase
import com.example.weatherviewer.data.usecase.GetFavouriteCityUseCase
import com.example.weatherviewer.data.usecase.GetSaveCityUseCase
import com.example.weatherviewer.data.usecase.SaveCityUseCase
import com.example.weatherviewer.data.usecase.SearchWeatherUseCase
import com.example.weatherviewer.utils.BASE_URL
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)

    }

    @Singleton
    @Provides
    fun provideNetworkConnectionInterceptor(@ApplicationContext context: Context): NetworkConnectionInterceptor {
        return NetworkConnectionInterceptor(context)
    }

    @Singleton
    @Provides
    fun provideLocalSource(weatherDetailDao: WeatherDetailDao): LocalSource {
        return LocalSource(weatherDetailDao)
    }

    @Singleton
    @Provides
    fun provideRemoteSource(apiInterface: ApiInterface): RemoteSource {
        return RemoteSource(apiInterface)
    }

    @Singleton
    @Provides
    fun provideRepoImpl(
        localSource: LocalSource,
        remoteSource: RemoteSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(remoteSource, localSource)

    }

    @InstallIn(SingletonComponent::class)
    @Module
    class DatabaseModule {

        @Provides
        fun provideVideoChannelDao(appDatabase: AppDatabase): WeatherDetailDao {
            return appDatabase.getWeatherDao()
        }


        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
            return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "AppDatabase"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }


    @Singleton
    @Provides
    fun provideSearchWeatherUseCase(repoImpl: WeatherRepository): SearchWeatherUseCase {
        return SearchWeatherUseCase(repoImpl)
    }

    @Singleton
    @Provides
    fun provideSaveCityUseCase(repoImpl: WeatherRepository): SaveCityUseCase {
        return SaveCityUseCase(repoImpl)
    }

    @Singleton
    @Provides
    fun provideGetSaveCityUseCase(repoImpl: WeatherRepository): GetSaveCityUseCase {
        return GetSaveCityUseCase(repoImpl)
    }

    @Singleton
    @Provides
    fun provideDeleteSaveCityUseCase(repoImpl: WeatherRepository): DeleteSaveCityUseCase {
        return DeleteSaveCityUseCase(repoImpl)
    }

    @Singleton
    @Provides
    fun provideGetFavouriteCityUseCase(repoImpl: WeatherRepository): GetFavouriteCityUseCase {
        return GetFavouriteCityUseCase(repoImpl)
    }



}