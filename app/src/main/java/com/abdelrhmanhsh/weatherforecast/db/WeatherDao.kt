package com.abdelrhmanhsh.weatherforecast.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weatherResponse: WeatherResponse)

    @Query("Select * From weather Where location = :location")
    fun getWeatherFromLocation(location: String): LiveData<WeatherResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeatherToFavourite(favouriteWeather: FavouriteWeather)

    @Query("Select * From favourite_weather")
    fun getFavourites(): LiveData<List<FavouriteWeather>>

    @Delete
    suspend fun deleteLocationFromFavourites(favouriteWeather: FavouriteWeather) // or pass location and query

}