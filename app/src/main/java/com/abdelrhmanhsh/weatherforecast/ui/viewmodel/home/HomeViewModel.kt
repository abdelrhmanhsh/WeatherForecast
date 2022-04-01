package com.abdelrhmanhsh.weatherforecast.ui.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abdelrhmanhsh.weatherforecast.model.RepositoryInterface
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

class HomeViewModel(private val repositoryInterface: RepositoryInterface): ViewModel() {

    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse> {
        return repositoryInterface.getWeather(latitude, longitude, units, lang, apiKey)
    }

}