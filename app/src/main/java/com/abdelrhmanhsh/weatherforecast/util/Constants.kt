package com.abdelrhmanhsh.weatherforecast.util

class Constants {

    companion object {

        const val BASE_URL = "https://api.openweathermap.org/"
        const val IMAGE_BASE_URL = "http://openweathermap.org/img/wn/"

        const val APP_DATABASE_NAME = "weather"

        const val LOCATION_PERMISSION_ID = 1

        const val USER_PREFERENCES_NAME = "user_preferences"
        const val USER_GPS_LOCATION_KEY = "user_gps_location_key"
        const val USER_MAP_LOCATION_KEY = "user_map_location_key"
        const val USER_FAV_LOCATION_KEY = "user_fav_location_key"
        const val GPS_LATITUDE_PREFERENCE_KEY = "gps_latitude_key"
        const val GPS_LONGITUDE_PREFERENCE_KEY = "gps_longitude_key"
        const val MAP_LATITUDE_PREFERENCE_KEY = "map_latitude_key"
        const val MAP_LONGITUDE_PREFERENCE_KEY = "map_longitude_key"
        const val FAV_LATITUDE_PREFERENCE_KEY = "fav_latitude_key"
        const val FAV_LONGITUDE_PREFERENCE_KEY = "fav_longitude_key"
        const val LAST_LATITUDE_PREFERENCE_KEY = "last_latitude_key"
        const val LAST_LONGITUDE_PREFERENCE_KEY = "last_longitude_key"
        const val USER_LAST_LOCATION_KEY = "last_location_key"
        const val IS_FAVOURITE_PREFERENCE_KEY = "is_favourite_key"
        const val LOCATION_PREFERENCE_KEY = "location_key"
        const val LANGUAGE_PREFERENCE_KEY = "language_key"
        const val TEMPERATURE_PREFERENCE_KEY = "temperature_key"
        const val WIND_SPEED_PREFERENCE_KEY = "wind_speed_key"

        const val MAPS_FROM_FAVOURITES = 1
        const val MAPS_FROM_SETTINGS = 2

        const val DATA_ALERT_ID = "data_alert_id"
        const val DATA_LOCATION = "data_location"
        const val DATA_LATITUDE = "data_latitude"
        const val DATA_LONGITUDE = "data_longitude"
        const val DATA_LANGUAGE = "data_language"
        const val DATA_UNITS = "data_units"
        const val DATA_START_MILLIS = "data_start_millis"
        const val DATA_END_MILLIS = "data_end_millis"
        const val DATA_FLAG = "data_flag"

        const val ALERT_CHANNEL = "alert"
        const val NOTIFICATION_ALERT_NAME = "weather alerts"
        const val ALERT_NOTIFICATION_ID = 1
        const val ALERT_ACTION = "alert_api_call"

    }
}