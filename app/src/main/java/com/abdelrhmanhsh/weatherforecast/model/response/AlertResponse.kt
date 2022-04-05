package com.abdelrhmanhsh.weatherforecast.model.response

data class AlertResponse(
    val description: String,
    val end: Int,
    val event: String,
    val sender_name: String,
    val start: Int,
    val tags: List<Any>
)