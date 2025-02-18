package com.example.weather.model

import com.example.weatherflows.model.CurrentWeather
import com.example.weatherflows.model.Location
import com.squareup.moshi.Json

data class WeatherResponse(
    @Json(name = "location") val location: Location,
    @Json(name = "current") val current: CurrentWeather
)


