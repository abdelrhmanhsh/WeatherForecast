package com.abdelrhmanhsh.weatherforecast.model

import androidx.lifecycle.LiveData
import com.abdelrhmanhsh.weatherforecast.model.response.Alert
import com.abdelrhmanhsh.weatherforecast.model.response.AlertChecker
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

interface RepositoryInterface {

    // network
    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse>
    fun getWeatherFavourite(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<FavouriteWeather>
    suspend fun getAlerts(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): AlertChecker

    // room
    suspend fun insertWeather(weatherResponse: WeatherResponse)
    fun getWeatherFromLocation(location: String): LiveData<WeatherResponse>

    suspend fun addWeatherToFavourites(favouriteWeather: FavouriteWeather)
    fun getFavourites(): LiveData<List<FavouriteWeather>>
    suspend fun deleteLocationFromFavourites(favouriteWeather: FavouriteWeather)

    suspend fun addAlert(alert: Alert)
    fun getAlerts(): LiveData<List<Alert>>
    suspend fun deleteAlert(alert: Alert)
    fun deleteAlertById(id: Long)

}