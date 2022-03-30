package com.abdelrhmanhsh.weatherforecast.ui

import androidx.fragment.app.Fragment

import android.os.Bundle
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

class MapsFragment : Fragment() {

    val TAG = "MapsFragment"

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var userPreferences: UserPreferences

    private val callback = OnMapReadyCallback { googleMap ->

        lifecycleScope.launch {
            val latitude = userPreferences.readLatitude() ?: 0.0
            val longitude = userPreferences.readLongitude() ?: 0.0
            println("Latitude Pref: $latitude Longitude: $longitude")

            val myLocation = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(myLocation))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10F))
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