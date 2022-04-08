package com.abdelrhmanhsh.weatherforecast.util

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.abdelrhmanhsh.weatherforecast.model.response.AlertChecker
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.network.WeatherService
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.ALERT_CHANNEL
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.ALERT_NOTIFICATION_ID
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.broadcasts.AlertWorkerReceiver
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.ALERT_ACTION
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_ALERT_ID
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_END_MILLIS
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_FLAG
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LANGUAGE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LATITUDE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LONGITUDE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_START_MILLIS
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_UNITS
import kotlinx.coroutines.*

class AlertWorker(
    val context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {

    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private val alertReceiver: AlertWorkerReceiver = AlertWorkerReceiver()

    override suspend fun doWork() = try {

        val incomingData = inputData
        val id = incomingData.getLong(DATA_ALERT_ID, -1)
        val latitude = incomingData.getDouble(DATA_LATITUDE, 0.0)
        val longitude = incomingData.getDouble(DATA_LONGITUDE, 0.0)
        val language = incomingData.getString(DATA_LANGUAGE)?: "en"
        val units = incomingData.getString(DATA_UNITS)?: "metric"
        val startMillis = incomingData.getLong(DATA_START_MILLIS, -1)
        val endMillis = incomingData.getLong(DATA_END_MILLIS, -1)

        sendToReceiver(id, latitude, longitude, language, units, startMillis, endMillis)

        withContext(Dispatchers.IO){
            getAlerts(
                latitude = latitude,
                longitude = longitude,
                units = units,
                lang = language,
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
            val response = weatherService.getAlerts(latitude, longitude, "daily, hourly, minutely", units, lang, apiKey)
            if (response.isSuccessful) {
                val message: String
                alertWeather = response.body()!!
                if(alertWeather.alerts.isNullOrEmpty()){
                    message = "Everything is fine"
                } else {
                    message = alertWeather.alerts[0].description
                }
                sendAlertNotification(message)
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

    private fun sendToReceiver(id: Long, latitude: Double, longitude: Double, language: String, units: String, startMillis: Long, endMillis: Long){
        val intentFilter = IntentFilter(ALERT_ACTION)
        applicationContext.registerReceiver(alertReceiver, intentFilter)
        var sendAgain = false

        // send alert for the next day
        // 86_400_000
        // 86_340_000
        if(endMillis - System.currentTimeMillis() >= 86_340_000){
            sendAgain = true
        }

        val intent = Intent()
        intent.putExtra(DATA_ALERT_ID, id)
        intent.putExtra(DATA_LATITUDE, latitude)
        intent.putExtra(DATA_LONGITUDE, longitude)
        intent.putExtra(DATA_LANGUAGE, language)
        intent.putExtra(DATA_UNITS, units)
        intent.putExtra(DATA_START_MILLIS, startMillis)
        intent.putExtra(DATA_END_MILLIS, endMillis)
        intent.putExtra(DATA_FLAG, sendAgain)
        intent.action = ALERT_ACTION
        applicationContext.sendBroadcast(intent)

    }

}