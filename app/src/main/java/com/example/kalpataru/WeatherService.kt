package com.example.kalpataru


import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") location: String,
        @Query("key") apiKey: String,
        @Query("aqi") aqi: String = "yes"
    ): WeatherApi
}