package com.abdelrhmanhsh.weatherforecast.util

import android.widget.ImageView
import com.abdelrhmanhsh.weatherforecast.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Extensions {

    companion object {

        fun ImageView.load(uri: String?){
            Glide.with(context)
                .load("${Constants.IMAGE_BASE_URL}$uri@2x.png")
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.weather_unknown)
                        .error(R.drawable.weather_unknown)
                )
                .into(this)
        }

        inline fun <reified T> Gson.fromJson(json: String) =
            fromJson<T>(json, object : TypeToken<T>() {}.type)

    }

}