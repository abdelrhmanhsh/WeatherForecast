package com.abdelrhmanhsh.weatherforecast.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.abdelrhmanhsh.weatherforecast.db.LocalSource
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import com.abdelrhmanhsh.weatherforecast.network.RemoteSource

class Repository(
    val context: Context,
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : RepositoryInterface  {

    companion object {

        private var repository: Repository? = null

        @Synchronized
        fun getInstance(
            context: Context,
            remoteSource: RemoteSource,
            localSource: LocalSource
        ): Repository? {

            if (repository == null) {
                repository = Repository(context, remoteSource, localSource)
            }
            return repository
        }
    }

    override fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse> {
        return remoteSource.getWeather(latitude, longitude, units, lang, apiKey)
    }

    override fun getWeatherFavourite(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        apiKey: String
    ): LiveData<FavouriteWeather> {
        return remoteSource.getWeatherFavourite(latitude, longitude, units, lang, apiKey)
    }

    override suspend fun insertWeather(weatherResponse: WeatherResponse) {
        localSource.insertWeather(weatherResponse)
    }

    override fun getWeatherFromLocation(location: String): LiveData<WeatherResponse> {
        return localSource.getWeatherFromLocation(location)
    }

    override suspend fun addWeatherToFavourites(favouriteWeather: FavouriteWeather) {
        localSource.addWeatherToFavourite(favouriteWeather)
    }

    override fun getFavourites(): LiveData<List<FavouriteWeather>> {
        return localSource.getFavourites()
    }

    override suspend fun deleteLocationFromFavourites(favouriteWeather: FavouriteWeather) {
        localSource.deleteLocationFromFavourites(favouriteWeather)
    }
}