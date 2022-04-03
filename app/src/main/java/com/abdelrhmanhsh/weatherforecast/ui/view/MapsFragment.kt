package com.abdelrhmanhsh.weatherforecast.ui.view

import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.db.ConcreteLocalSource
import com.abdelrhmanhsh.weatherforecast.model.Repository
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.favourites.FavouritesViewModel
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.favourites.FavouritesViewModelFactory
import com.abdelrhmanhsh.weatherforecast.util.AppHelper.Companion.isInternetAvailable
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.MAPS_FROM_FAVOURITES
import com.abdelrhmanhsh.weatherforecast.util.PrivateConstants.Companion.API_KEY
import com.abdelrhmanhsh.weatherforecast.util.UserPreferences
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
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
    private var flag: Int = -1

    private lateinit var viewModel: FavouritesViewModel
    private lateinit var viewModelFactory: FavouritesViewModelFactory

    private val callback = OnMapReadyCallback { googleMap ->

        lifecycleScope.launch {
            val latitude = userPreferences.readGPSLatitude() ?: 0.0 //TODO replace this with readMapLatitude
            val longitude = userPreferences.readGPSLongitude() ?: 0.0
            println("Latitude Pref: $latitude Longitude: $longitude")

            val myLocation = LatLng(latitude, longitude)
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
            googleMap.addMarker(MarkerOptions().position(loc))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10F))

            if(flag == MAPS_FROM_FAVOURITES){
                Toast.makeText(context, "Coming from favourites", Toast.LENGTH_SHORT).show()
                if (isInternetAvailable(context!!)){
                    lifecycleScope.launch {

                        val temperature: String = userPreferences.readTemperature() ?: getString(R.string.celsius)

                        val units = when (temperature) {
                            getString(R.string.celsius) -> getString(R.string.metric)
                            getString(R.string.fahrenheit) -> getString(R.string.imperial)
                            else -> ""
                        }

                        var language: String = userPreferences.readLanguage()?: "en"

                        if(userPreferences.readLanguage() == getString(R.string.arabic)){
                            language = "ar"
                        }

                        showDialogFav(city, country, it.latitude, it.longitude, units, language, googleMap)

                    }
                } else {
                    Toast.makeText(context, "Connect to Internet First to this Operation!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Coming from settings", Toast.LENGTH_SHORT).show()
                showDialogSettings(city, country, it.latitude, it.longitude, googleMap)
            }

        }
    }

    private fun showDialogFav(city: String, country: String, latitude: Double, longitude: Double, units: String, language: String, googleMap: GoogleMap){

        val builder = AlertDialog.Builder(context!!)
        builder.apply {
            setTitle(city)
            setMessage(getString(R.string.add_this_location))
            setPositiveButton(getString(R.string.save)) { dialog, which ->

                viewModel.getWeather(latitude, longitude, units, language, API_KEY).observe(this@MapsFragment){ weather ->
                    println("weather from fav dialog: $weather")
                    val userLocation = "$city, $country"
                    val saveWeather = FavouriteWeather(
                        location = userLocation,
                        current = weather.current,
                        daily = weather.daily,
                        hourly = weather.hourly,
                        lat = weather.lat,
                        lon = weather.lon,
                        timezone = weather.timezone,
                        timezone_offset = weather.timezone_offset
                    )
                    viewModel.addWeatherToFavourites(saveWeather)
                }

                val action =
                    MapsFragmentDirections.actionMapsTFavourites()
                Navigation.findNavController(view!!).navigate(action)

            }
            setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                googleMap.clear()
            }
            show()
        }

    }

    private fun showDialogSettings(city: String, country: String, latitude: Double, longitude: Double, googleMap: GoogleMap){
        val builder = AlertDialog.Builder(context!!)
        builder.apply {
            setTitle(city)
            setMessage(getString(R.string.add_this_location))
            setPositiveButton(getString(R.string.save)) { dialog, which ->

                lifecycleScope.launch {
                    userPreferences.storeMapLongLatPref(latitude, longitude)
                    userPreferences.storeLastLongLatPref(latitude, longitude)
                    userPreferences.storeUserMapLocationPref("$city, $country")
                    userPreferences.storeIsFavouritePref(false)
                }
                val action =
                    MapsFragmentDirections.actionMapsToHome()
                Navigation.findNavController(view!!).navigate(action)

            }
            setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                googleMap.clear()
            }
            show()
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

        val args: MapsFragmentArgs =
            MapsFragmentArgs.fromBundle(arguments!!)
        flag = args.flag

        viewModelFactory = FavouritesViewModelFactory(
            Repository.getInstance(
                context!!,
                WeatherClient.getInstance()!!,
                ConcreteLocalSource(context!!)
            )!!
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(FavouritesViewModel::class.java)
    }
}