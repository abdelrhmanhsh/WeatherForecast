package com.abdelrhmanhsh.weatherforecast.network

import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/onecall")
    suspend fun getWeather(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double,
        @Query("exclude")
        exclude: String,
        @Query("appid")
        appid: String
    ): Response<WeatherResponse>

}