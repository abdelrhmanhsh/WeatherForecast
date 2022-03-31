//package com.abdelrhmanhsh.weatherforecast.network
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class ApiCalls: RemoteSource {
//
//    var weatherList = MutableLiveData<List<WeatherResponse>>()
//
//    override fun getWeather(): LiveData<List<WeatherResponse>> {
//        val weatherService = WeatherClient.getInstanceRetrofit().create(WeatherService::class.java)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = weatherService.getWeather()
//            if (response.isSuccessful) {
//                weatherList.postValue(response.body())
//                println("ApiCalls response: ${response.body()}")
//            } else {
//                println("ApiCalls response: ${response.message()}")
//            }
//        }
//        return weatherList
//    }
//
//}