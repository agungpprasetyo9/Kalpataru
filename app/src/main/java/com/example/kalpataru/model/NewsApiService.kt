package com.example.kalpataru.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
interface NewsApiService
{
    @GET("everything")
    suspend fun getTopHeadLines(
        @Query("apiKey") apiKey : String,
        @Query("q") query : String
    ) : Response<NewsResponse>
}