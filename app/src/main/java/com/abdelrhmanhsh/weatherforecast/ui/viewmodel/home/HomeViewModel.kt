package com.abdelrhmanhsh.weatherforecast.ui.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrhmanhsh.weatherforecast.model.RepositoryInterface
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import kotlinx.coroutines.launch

class HomeViewModel(private val repositoryInterface: RepositoryInterface): ViewModel() {

    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse> {
        return repositoryInterface.getWeather(latitude, longitude, units, lang, apiKey)
    }

    fun insertWeather(weatherResponse: WeatherResponse){
        viewModelScope.launch {
            repositoryInterface.insertWeather(weatherResponse)
        }
    }

    fun getWeatherFromLocation(location: String): LiveData<WeatherResponse> {
        return repositoryInterface.getWeatherFromLocation(location)
    }

}