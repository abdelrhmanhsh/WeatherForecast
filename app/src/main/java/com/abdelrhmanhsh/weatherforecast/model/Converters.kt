package com.abdelrhmanhsh.weatherforecast.model

import androidx.room.TypeConverter
import com.abdelrhmanhsh.weatherforecast.model.response.*
import com.google.gson.Gson
import java.lang.Exception
import com.abdelrhmanhsh.weatherforecast.util.Extensions.Companion.fromJson

class Converters {

    @TypeConverter
    fun fromCurrent(current: Current): String{
        return Gson().toJson(current)
    }

    @TypeConverter
    fun toCurrent(current: String): Current{
        return Gson().fromJson(current, Current::class.java)
    }

    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>): String {
        return Gson().toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(weatherList: String): List<Weather>{
        return try {
            Gson().fromJson<List<Weather>>(weatherList)
        } catch (e: Exception){
            arrayListOf()
        }
    }

    @TypeConverter
    fun fromDailyList(dailyList: List<Daily>): String {
        return Gson().toJson(dailyList)
    }

    @TypeConverter
    fun toDailyList(dailyList: String): List<Daily>{
        return try {
            Gson().fromJson<List<Daily>>(dailyList)
        } catch (e: Exception){
            arrayListOf()
        }
    }

    @TypeConverter
    fun fromHourlyList(hourlyList: List<Hourly>): String {
        return Gson().toJson(hourlyList)
    }

    @TypeConverter
    fun toHourlyList(hourlyList: String): List<Hourly>{
        return try {
            Gson().fromJson<List<Hourly>>(hourlyList)
        } catch (e: Exception){
            arrayListOf()
        }
    }

    @TypeConverter
    fun fromAlertList(alertList: List<AlertResponse>): String {
        return Gson().toJson(alertList)
    }

    @TypeConverter
    fun toAlertList(alertList: String): List<AlertResponse>{
        return try {
            Gson().fromJson<List<AlertResponse>>(alertList)
        } catch (e: Exception){
            arrayListOf()
        }
    }

}