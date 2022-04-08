package com.abdelrhmanhsh.weatherforecast.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.abdelrhmanhsh.weatherforecast.db.ConcreteLocalSource
import com.abdelrhmanhsh.weatherforecast.model.Repository
import com.abdelrhmanhsh.weatherforecast.model.RepositoryInterface
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.util.AlertWorker
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_ALERT_ID
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_END_MILLIS
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_FLAG
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LANGUAGE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LATITUDE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LONGITUDE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_START_MILLIS
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_UNITS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AlertWorkerReceiver : BroadcastReceiver() {

    lateinit var repoInterface: RepositoryInterface

    override fun onReceive(context: Context, intent: Intent) {

        repoInterface = Repository.getInstance(
            context,
            WeatherClient.getInstance()!!,
            ConcreteLocalSource(context)
        )!!

        val id = intent.getLongExtra(DATA_ALERT_ID, -1L)
        val latitude = intent.getDoubleExtra(DATA_LATITUDE, 0.0)
        val longitude = intent.getDoubleExtra(DATA_LONGITUDE, 0.0)
        val language = intent.getStringExtra(DATA_LANGUAGE)?: "en"
        val units = intent.getStringExtra(DATA_UNITS)?: "metric"
        val startMillis = intent.getLongExtra(DATA_START_MILLIS, -1)
        val endMillis = intent.getLongExtra(DATA_END_MILLIS, -1)
        val sendAgain = intent.getBooleanExtra(DATA_FLAG, false)

        if(sendAgain) {
            sendAlertWorker(context, id, latitude, longitude, language, units, startMillis, endMillis)
        } else {
            deleteAlertFromDatabase(id)
        }

    }

    private fun sendAlertWorker(context: Context, id: Long, latitude: Double, longitude: Double, language: String,
                                units: String, startMillis: Long, endMillis: Long){

        val data = Data.Builder()
            .putLong(DATA_ALERT_ID, id)
            .putDouble(DATA_LATITUDE, latitude)
            .putDouble(DATA_LONGITUDE, longitude)
            .putString(DATA_LANGUAGE, language)
            .putString(DATA_UNITS, units)
            .putLong(DATA_START_MILLIS, startMillis)
            .putLong(DATA_END_MILLIS, endMillis)
            .build()

        val networkConstraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val coroutinesWorker = OneTimeWorkRequestBuilder<AlertWorker>()
            .setInputData(data)
            .setConstraints(networkConstraint)
            .setInitialDelay(1, TimeUnit.DAYS)
            .addTag(id.toString())
            .build()

        WorkManager.getInstance(context).enqueue(coroutinesWorker)

    }

    private fun deleteAlertFromDatabase(id: Long){
        if(id != -1L){
            CoroutineScope(IO).launch {
                repoInterface.deleteAlertById(id)
            }
        }
    }

}