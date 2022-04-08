package com.abdelrhmanhsh.weatherforecast.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert")
data class Alert(

    @PrimaryKey
    val id: Long,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
    val startDateMillis: Long,
    val endDateMillis: Long,
    val latitude: Double,
    val longitude: Double

)