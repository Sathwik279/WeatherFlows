package com.example.weatherflows.model

import com.squareup.moshi.Json

data class HomeWeatherResponse(
    @Json(name = "location") val location: Location,
    @Json(name = "current") val current: CurrentWeather
)

