package com.abdelrhmanhsh.weatherforecast.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_weather")
data class FavouriteWeather(
    @PrimaryKey
    val location: String,
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)