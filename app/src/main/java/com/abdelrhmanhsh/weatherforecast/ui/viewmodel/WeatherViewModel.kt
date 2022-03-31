package com.abdelrhmanhsh.weatherforecast.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abdelrhmanhsh.weatherforecast.model.RepositoryInterface
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

class WeatherViewModel(private val repositoryInterface: RepositoryInterface): ViewModel() {

    fun getWeather(latitude: Double, longitude: Double, exclude: String, apiKey: String): LiveData<WeatherResponse> {
        return repositoryInterface.getWeather(latitude, longitude, exclude, apiKey)
    }

}