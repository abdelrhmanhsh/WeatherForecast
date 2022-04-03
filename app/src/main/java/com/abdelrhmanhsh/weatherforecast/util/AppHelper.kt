package com.abdelrhmanhsh.weatherforecast.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


class AppHelper {

    companion object{

        fun isInternetAvailable(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val n = cm.activeNetwork
            if (n != null) {
                val nc = cm.getNetworkCapabilities(n)
                return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
            return false
        }
    }
}