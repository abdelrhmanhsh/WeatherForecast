package com.abdelrhmanhsh.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.abdelrhmanhsh.weatherforecast.databinding.ActivityMainBinding
import com.abdelrhmanhsh.weatherforecast.util.Constants
import com.google.android.gms.location.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import android.location.Geocoder
import android.util.Log
import java.util.*


class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        initNavDrawer()
        initNavGraph()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        firebaseAnalytics = Firebase.analytics

    }

    private fun initNavDrawer(){
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initNavGraph(){
        navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        if(checkedPermissions()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { location ->
//                    val location: Location = it.result
                    if(location.result == null){
                        requestNewLocation()
                    } else {
                        Toast.makeText(this, "Latitude: ${location.result.latitude} Longitude ${location.result.longitude}",
                        Toast.LENGTH_SHORT).show()

                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses: List<Address> = geocoder.getFromLocation(location.result.latitude, location.result.longitude, 1)

                        val cityName: String = addresses[0].getAddressLine(0) ?: ""
                        val stateName: String = addresses[0].getAddressLine(1) ?: ""
                        val countryName: String = addresses[0].getAddressLine(2) ?: ""

                        val city: String = addresses[0].locality ?: ""
                        val country: String = addresses[0].countryName ?: ""

                        Log.i(TAG, "getLastLocation: FULL LOCATION: City: ${city.take(15)}, country: $country")
                    }
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkedPermissions() : Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun enableLocationManager(){
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation(){
        val locationRequest: LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5)
            .setFastestInterval(0)
            .setNumUpdates(1)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            Toast.makeText(this@MainActivity, "Latitude: ${location.latitude} Longitude ${location.longitude}",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ), Constants.LOCATION_PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.LOCATION_PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(!isLocationEnabled())
            enableLocationManager()
    }
}