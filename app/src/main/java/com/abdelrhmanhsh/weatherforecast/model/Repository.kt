package com.abdelrhmanhsh.weatherforecast.model

import android.content.Context
import androidx.lifecycle.LiveData
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import com.abdelrhmanhsh.weatherforecast.network.RemoteSource

class Repository(
    val context: Context,
    private val remoteSource: RemoteSource
) : RepositoryInterface  {

    companion object {

        private var repository: Repository? = null

        @Synchronized
        fun getInstance(
            context: Context,
            remoteSource: RemoteSource
        ): Repository? {

            if (repository == null) {
                repository = Repository(context, remoteSource)
            }
            return repository
        }
    }

    override fun getWeather(latitude: Double, longitude: Double, exclude: String, apiKey: String): LiveData<WeatherResponse> {
        return remoteSource.getWeather(latitude, longitude, exclude, apiKey)
    }
}