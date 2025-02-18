package com.example.weatherflows.model

import com.squareup.moshi.Json

data class Location(
    @Json(name = "name") val name: String
)