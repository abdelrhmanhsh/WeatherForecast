package com.abdelrhmanhsh.weatherforecast.network

import androidx.lifecycle.LiveData
import com.abdelrhmanhsh.weatherforecast.model.response.AlertChecker
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

interface RemoteSource {

    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse>
    fun getWeatherFavourite(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<FavouriteWeather>
    suspend fun getAlerts(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): AlertChecker

}