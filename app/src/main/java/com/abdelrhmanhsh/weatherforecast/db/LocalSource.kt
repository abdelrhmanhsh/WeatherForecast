package com.abdelrhmanhsh.weatherforecast.db

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Query
import com.abdelrhmanhsh.weatherforecast.model.Alert
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

interface LocalSource {

    suspend fun insertWeather(weatherResponse: WeatherResponse)
    fun getWeatherFromLocation(location: String): LiveData<WeatherResponse>

    fun getFavourites(): LiveData<List<FavouriteWeather>>
    suspend fun addWeatherToFavourite(favouriteWeather: FavouriteWeather)
    suspend fun deleteLocationFromFavourites(favouriteWeather: FavouriteWeather)

    suspend fun addAlert(alert: Alert)
    fun getAlerts(): LiveData<List<Alert>>
    suspend fun deleteAlert(alert: Alert)

}