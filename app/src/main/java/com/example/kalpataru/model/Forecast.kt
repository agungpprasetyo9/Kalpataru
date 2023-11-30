package com.example.kalpataru.model

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    // Add other properties as needed
)

data class Astro(
    val sunrise: String,
    val sunset: String
)

data class Hour(
    val time_epoch: Long,
    val temp_c: Double,
    val condition: Condition,
    // Add other properties as needed
)
