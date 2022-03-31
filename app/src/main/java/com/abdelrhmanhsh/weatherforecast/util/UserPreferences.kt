package com.abdelrhmanhsh.weatherforecast.util

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(
    name = Constants.USER_PREFERENCES_NAME
)

class UserPreferences(val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val userLocationPref = stringPreferencesKey(Constants.USER_LOCATION_KEY)
        private val gpsLatitudePref = doublePreferencesKey(Constants.GPS_LATITUDE_PREFERENCE_KEY)
        private val gpsLongitudePref = doublePreferencesKey(Constants.GPS_LONGITUDE_PREFERENCE_KEY)
        private val mapLatitudePref = doublePreferencesKey(Constants.MAP_LATITUDE_PREFERENCE_KEY)
        private val mapLongitudePref = doublePreferencesKey(Constants.MAP_LONGITUDE_PREFERENCE_KEY)

        private val locationPref = stringPreferencesKey(Constants.LOCATION_PREFERENCE_KEY)
        private val languagePref = stringPreferencesKey(Constants.LANGUAGE_PREFERENCE_KEY)
        private val temperaturePref = stringPreferencesKey(Constants.TEMPERATURE_PREFERENCE_KEY)
        private val windSpeedPref = stringPreferencesKey(Constants.WIND_SPEED_PREFERENCE_KEY)
    }

    suspend fun storeUserLocationPref(location: String){
        dataStore.edit {
            it[userLocationPref] = location
        }
    }

    suspend fun storeGPSLongLatPref(latitude: Double, longitude: Double){
        dataStore.edit {
            it[gpsLatitudePref] = latitude
            it[gpsLongitudePref] = longitude
        }
    }

    suspend fun storeMapLongLatPref(latitude: Double, longitude: Double){
        dataStore.edit {
            it[mapLatitudePref] = latitude
            it[mapLongitudePref] = longitude
        }
    }

    suspend fun storeLocationPref(location: String){
        dataStore.edit {
            it[locationPref] = location
        }
    }

    suspend fun storeLanguagePref(language: String){
        dataStore.edit {
            it[languagePref] = language
        }
    }

    suspend fun storeTemperaturePref(temperature: String){
        dataStore.edit {
            it[temperaturePref] = temperature
        }
    }

    suspend fun storeWindSpeedPref(windSpeed: String){
        dataStore.edit {
            it[windSpeedPref] = windSpeed
        }
    }

    suspend fun readUserLocation(): String?{
        val location = dataStore.data.first()
        return location[userLocationPref]
    }

    suspend fun readGPSLatitude(): Double?{
        val latitude = dataStore.data.first()
        return latitude[gpsLatitudePref]
    }

    suspend fun readGPSLongitude(): Double?{
        val longitude = dataStore.data.first()
        return longitude[gpsLongitudePref]
    }

    suspend fun readMapLatitude(): Double?{
        val latitude = dataStore.data.first()
        return latitude[mapLatitudePref]
    }

    suspend fun readMapLongitude(): Double?{
        val longitude = dataStore.data.first()
        return longitude[mapLongitudePref]
    }

    suspend fun readLocation(): String?{
        val location = dataStore.data.first()
        return location[locationPref]
    }

    suspend fun readLanguage(): String?{
        val language = dataStore.data.first()
        return language[languagePref]
    }

    suspend fun readTemperature(): String?{
        val temperature = dataStore.data.first()
        return temperature[temperaturePref]
    }

    suspend fun readWindSpeed(): String?{
        val windSpeed = dataStore.data.first()
        return windSpeed[windSpeedPref]
    }

}