package com.abdelrhmanhsh.weatherforecast.model

import androidx.lifecycle.LiveData
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

interface RepositoryInterface {

    // network
    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse>
    fun getWeatherFavourite(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<FavouriteWeather>

    // room
    suspend fun insertWeather(weatherResponse: WeatherResponse)
    fun getWeatherFromLocation(location: String): LiveData<WeatherResponse>
    suspend fun addWeatherToFavourites(favouriteWeather: FavouriteWeather)
    fun getFavourites(): LiveData<List<FavouriteWeather>>
    suspend fun deleteLocationFromFavourites(favouriteWeather: FavouriteWeather)

}