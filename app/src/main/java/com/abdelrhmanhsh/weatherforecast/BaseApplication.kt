package com.abdelrhmanhsh.weatherforecast

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.ALERT_CHANNEL
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.NOTIFICATION_ALERT_NAME

class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL,
                NOTIFICATION_ALERT_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            alertChannel.description = getString(R.string.alert_notification_description)
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(alertChannel)

        }

    }

}