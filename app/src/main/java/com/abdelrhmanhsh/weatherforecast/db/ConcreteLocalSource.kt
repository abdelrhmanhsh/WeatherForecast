package com.abdelrhmanhsh.weatherforecast.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.abdelrhmanhsh.weatherforecast.model.response.Alert
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

class ConcreteLocalSource(val context: Context): LocalSource {

    private var dao: WeatherDao

    init {
        val db: AppDatabase? = AppDatabase.getInstance(context.applicationContext)
        dao = db!!.weatherDao()
    }

    override suspend fun insertWeather(weatherResponse: WeatherResponse) {
        dao.insertWeather(weatherResponse)
    }

    override fun getWeatherFromLocation(location: String): LiveData<WeatherResponse> {
        return dao.getWeatherFromLocation(location)
    }

    override fun getFavourites(): LiveData<List<FavouriteWeather>> {
        return dao.getFavourites()
    }

    override suspend fun addWeatherToFavourite(favouriteWeather: FavouriteWeather) {
        dao.addWeatherToFavourite(favouriteWeather)
    }

    override suspend fun deleteLocationFromFavourites(favouriteWeather: FavouriteWeather) {
        dao.deleteLocationFromFavourites(favouriteWeather)
    }

    override suspend fun addAlert(alert: Alert) {
        dao.addAlert(alert)
    }

    override fun getAlerts(): LiveData<List<Alert>> {
        return dao.getAlerts()
    }

    override suspend fun deleteAlert(alert: Alert) {
        dao.deleteAlert(alert)
    }

    override fun deleteAlertById(id: Long) {
        dao.deleteAlertById(id)
    }

}