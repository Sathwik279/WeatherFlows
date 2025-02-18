package com.example.weatherflows.model

import com.squareup.moshi.Json


data class CurrentWeather(
    @Json(name = "temp_c") val temp_c: Double // here the name of the variable also must be same as the name in the Json annotation
)