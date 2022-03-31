package com.abdelrhmanhsh.weatherforecast.ui.view

import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.util.UserPreferences
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class MapsFragment : Fragment() {

    val TAG = "MapsFragment"

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var userPreferences: UserPreferences

    private val callback = OnMapReadyCallback { googleMap ->

        lifecycleScope.launch {
            val latitude = userPreferences.readGPSLatitude() ?: 0.0
            val longitude = userPreferences.readGPSLongitude() ?: 0.0
            println("Latitude Pref: $latitude Longitude: $longitude")

            val myLocation = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(myLocation))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 5F))
        }

        googleMap.setOnMapClickListener {
            println("On map click: lat: ${it.latitude}, long: ${it.longitude}")

            var city = ""
            var country = ""

            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(it.latitude, it.longitude, 1)

                city = addresses[0].locality ?: ""
                country = addresses[0].countryName ?: ""

                Log.i(TAG, "getLastLocation: FULL LOCATION: City: ${city.take(15)}, country: $country")
            } catch (e: IOException){
                Log.e(TAG, "error: ${e.message}")
            }

            val loc = LatLng(it.latitude, it.longitude)
            googleMap.addMarker(MarkerOptions().position(loc).title(city))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10F))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        userPreferences = UserPreferences(context!!)
    }
}