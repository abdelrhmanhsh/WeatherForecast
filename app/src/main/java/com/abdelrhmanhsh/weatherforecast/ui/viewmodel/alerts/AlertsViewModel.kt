package com.abdelrhmanhsh.weatherforecast.ui.viewmodel.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrhmanhsh.weatherforecast.model.response.Alert
import com.abdelrhmanhsh.weatherforecast.model.RepositoryInterface
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import kotlinx.coroutines.launch

class AlertsViewModel(private val repositoryInterface: RepositoryInterface): ViewModel() {

    fun getWeather(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String): LiveData<WeatherResponse> {
        return repositoryInterface.getWeather(latitude, longitude, units, lang, apiKey)
    }

    fun addAlert(alert: Alert){
        viewModelScope.launch {
            repositoryInterface.addAlert(alert)
        }
    }

    fun getAlerts(): LiveData<List<Alert>> {
        return repositoryInterface.getAlerts()
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            repositoryInterface.deleteAlert(alert)
        }
    }

}