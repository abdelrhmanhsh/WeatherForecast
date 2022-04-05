package com.abdelrhmanhsh.weatherforecast.util

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.abdelrhmanhsh.weatherforecast.model.response.AlertChecker
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.network.WeatherService
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.ALERT_CHANNEL
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.ALERT_NOTIFICATION_ID
import com.abdelrhmanhsh.weatherforecast.R
import kotlinx.coroutines.*

class AlertWorker(
    val context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {

    lateinit var notificationManagerCompat: NotificationManagerCompat

    override suspend fun doWork() = try {

    withContext(Dispatchers.IO){
        getAlerts(
            latitude = 62.578479,
            longitude = 29.787396,
            units = "metric",
            lang = "en",
            apiKey = PrivateConstants.API_KEY
        )
    }

        Result.success()
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure()
    }

    lateinit var alertWeather: AlertChecker

    fun getAlerts(latitude: Double, longitude: Double, units: String, lang: String, apiKey: String) {
        val weatherService = WeatherClient.getInstanceRetrofit().create(WeatherService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = weatherService.getAlerts(latitude, longitude, "minutely", units, lang, apiKey)
            if (response.isSuccessful) {
                alertWeather = response.body()!!
                if(alertWeather.alerts.isNullOrEmpty()){
                    sendAlertNotification("Everything is fine")
                } else {
                    sendAlertNotification(alertWeather.alerts[0].description)
                }

            }
        }
    }

    private fun sendAlertNotification(description: String){
        notificationManagerCompat = NotificationManagerCompat.from(context)

        val notification: Notification = NotificationCompat.Builder(context, ALERT_CHANNEL)
            .setSmallIcon(R.drawable.weather_unknown)
            .setContentTitle("Alert Notification")
            .setContentText(description)
            .setAutoCancel(true)
            .build()

        notificationManagerCompat.notify(ALERT_NOTIFICATION_ID, notification)
    }

}