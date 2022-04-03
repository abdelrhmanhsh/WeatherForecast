package com.abdelrhmanhsh.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert")
data class Alert(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
    val startDateMillis: Long,
    val endDateMillis: Long,
    val latitude: Double,
    val longitude: Double

)