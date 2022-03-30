package com.abdelrhmanhsh.weatherforecast.util

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(
    name = Constants.USER_PREFERENCES_NAME
)

class UserPreferences(val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val latitudePref = doublePreferencesKey(Constants.LATITUDE_PREFERENCE_KEY)
        private val longitudePref = doublePreferencesKey(Constants.LONGITUDE_PREFERENCE_KEY)
    }

    suspend fun storeLongLatPref(latitude: Double, longitude: Double){
        dataStore.edit {
            it[latitudePref] = latitude
            it[longitudePref] = longitude
        }
    }

    suspend fun readLatitude(): Double?{
        val latitude = dataStore.data.first()
        return latitude[latitudePref]
    }

    suspend fun readLongitude(): Double?{
        val longitude = dataStore.data.first()
        return longitude[longitudePref]
    }

}