package com.abdelrhmanhsh.weatherforecast.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abdelrhmanhsh.weatherforecast.model.response.AlertChecker
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.BASE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherClient: RemoteSource {

    companion object {

        private lateinit var retrofit: Retrofit

        @Synchronized
        fun getInstanceRetrofit(): Retrofit {

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit
        }

        private var client: WeatherClient? = null

        fun getInstance(): WeatherClient? {
            if (client == null) {
                client = WeatherClient()
            }
            return client
        }
    }

    var weather = MutableLiveData<WeatherResponse>()
    var favouriteWeather = MutableLiveData<FavouriteWeather>()
    lateinit var alertWeather: AlertChecker

    override fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse> {
        val weatherService = getInstanceRetrofit().create(WeatherService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = weatherService.getWeather(latitude, longitude, "minutely", units, lang, apiKey)
            if (response.isSuccessful) {
                weather.postValue(response.body())
                println("ApiCalls response: ${response.body()}")
            } else {
                println("ApiCalls response: ${response.message()}")
            }
        }
        return weather
    }

    override fun getWeatherFavourite(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        apiKey: String
    ): LiveData<FavouriteWeather> {
        val weatherService = getInstanceRetrofit().create(WeatherService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = weatherService.getWeatherFavourite(latitude, longitude, "minutely", units, lang, apiKey)
            if (response.isSuccessful) {
                favouriteWeather.postValue(response.body())
                println("ApiCalls response: ${response.body()}")
            } else {
                println("ApiCalls response: ${response.message()}")
            }
        }
        return favouriteWeather
    }

    override suspend fun getAlerts(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): AlertChecker {
        val weatherService = getInstanceRetrofit().create(WeatherService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = weatherService.getAlerts(latitude, longitude, "minutely", units, lang, apiKey)
            if (response.isSuccessful) {
                alertWeather = response.body()!!
            }
        }
        return alertWeather
    }

}