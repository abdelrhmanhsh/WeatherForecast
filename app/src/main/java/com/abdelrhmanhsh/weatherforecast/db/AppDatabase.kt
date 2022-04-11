package com.abdelrhmanhsh.weatherforecast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abdelrhmanhsh.weatherforecast.model.response.Alert
import com.abdelrhmanhsh.weatherforecast.model.Converters
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.APP_DATABASE_NAME

@Database(entities = [WeatherResponse::class, FavouriteWeather::class, Alert::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {

        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, APP_DATABASE_NAME
                )
                    .build()
            }
            return instance
        }
    }

}