package com.abdelrhmanhsh.weatherforecast.model.response

data class AlertChecker(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val alerts: List<AlertResponse>
)