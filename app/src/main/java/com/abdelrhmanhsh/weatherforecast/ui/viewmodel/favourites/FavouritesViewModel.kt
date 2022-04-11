package com.abdelrhmanhsh.weatherforecast.ui.viewmodel.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrhmanhsh.weatherforecast.model.RepositoryInterface
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import kotlinx.coroutines.launch

class FavouritesViewModel(private val repositoryInterface: RepositoryInterface): ViewModel() {

    fun addWeatherToFavourites(favouriteWeather: FavouriteWeather){
        viewModelScope.launch {
            repositoryInterface.addWeatherToFavourites(favouriteWeather)
        }
    }

    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<FavouriteWeather> {
        return repositoryInterface.getWeatherFavourite(latitude, longitude, units, lang, apiKey)
    }

    fun getFavourites(): LiveData<List<FavouriteWeather>> {
        return repositoryInterface.getFavourites()
    }

    fun deleteLocationFromFavourites(favouriteWeather: FavouriteWeather) {
        viewModelScope.launch {
            repositoryInterface.deleteLocationFromFavourites(favouriteWeather)
        }
    }

}