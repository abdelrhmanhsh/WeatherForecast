package com.abdelrhmanhsh.weatherforecast.network

import androidx.lifecycle.LiveData
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

interface RemoteSource {

    fun getWeather(latitude: Double, longitude: Double, exclude: String, apiKey: String): LiveData<WeatherResponse>

}