package com.example.weatherflows.api

import com.example.weather.model.WeatherResponse
import com.example.weatherflows.model.HomeWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") aqi: String
    ): WeatherResponse

    @GET("current.json")
    suspend fun getHomeCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") aqi: String
    ): HomeWeatherResponse
}


