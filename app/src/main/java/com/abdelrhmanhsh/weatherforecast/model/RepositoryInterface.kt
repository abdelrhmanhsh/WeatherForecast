package com.abdelrhmanhsh.weatherforecast.model

import androidx.lifecycle.LiveData
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

interface RepositoryInterface {

    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse>

}