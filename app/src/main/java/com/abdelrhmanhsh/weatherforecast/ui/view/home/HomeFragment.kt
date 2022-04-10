package com.abdelrhmanhsh.weatherforecast.ui.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.databinding.FragmentHomeBinding
import com.abdelrhmanhsh.weatherforecast.db.ConcreteLocalSource
import com.abdelrhmanhsh.weatherforecast.model.Repository
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.home.HomeViewModel
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.home.HomeViewModelFactory
import com.abdelrhmanhsh.weatherforecast.util.*
import com.abdelrhmanhsh.weatherforecast.util.AppHelper.Companion.isInternetAvailable
import com.abdelrhmanhsh.weatherforecast.util.Extensions.Companion.load
import com.abdelrhmanhsh.weatherforecast.util.PrivateConstants.Companion.API_KEY
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    val TAG = "HomeFragment"

    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var userPreferences: UserPreferences

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(requireContext())
        viewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                requireContext(),
                WeatherClient.getInstance()!!,
                ConcreteLocalSource(requireContext())
            )!!
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
//        getWeather()

        initRecyclerViews()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        getLastLocation()

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        println("getLastLocation")
        if(checkedPermissions()){
            println("getLastLocation checkedPermissions")
            if(isLocationEnabled()){
                println("getLastLocation checkedPermissions isLocationEnabled")
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { location ->
//                    val location: Location = it.result
                    if(location.result == null){
                        requestNewLocation()
                    } else {
//                        Toast.makeText(requireContext(), "LAST LOCATION Latitude: ${location.result.latitude} Longitude ${location.result.longitude}",
//                        Toast.LENGTH_SHORT).show()

                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address> = geocoder.getFromLocation(location.result.latitude, location.result.longitude, 1)

                        val city: String = addresses[0].locality ?: ""
                        val country: String = addresses[0].countryName ?: ""

                        CoroutineScope(IO).launch {
                            // take 15
                            val job = lifecycleScope.launch {
                                userPreferences.storeUserGPSLocationPref("$city, $country")
                                userPreferences.storeUserLastLocationPref("$city, $country")
                                userPreferences.storeGPSLongLatPref(location.result.latitude, location.result.longitude)
                                userPreferences.storeLastLongLatPref(location.result.latitude, location.result.longitude)
//                                userPreferences.storeIsFavouritePref(false)
                            }

                            Log.i(TAG, "getLastLocation: FULL LOCATION: City: ${city.take(15)}, country: $country")
                            Log.i(TAG, "getLastLocation: Latitude: ${location.result.latitude}, Longitude: ${location.result.longitude}")
                            println("before job ${location.result.latitude}")
                            job.join()
                            println("after job ${location.result.latitude}")
                            getWeather()
                        }
                    }
                }
            }
        } else {
            println("getLastLocation requestPermissions")
            requestPermissions()
        }
    }

    private fun checkedPermissions() : Boolean {
        return (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun enableLocationManager(){
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation(){
        println("requestNewLocation")
        val locationRequest: LocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(5)
            .setFastestInterval(0)
            .setNumUpdates(1)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()!!)
        getWeather()
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            println("location Callback: ${location.latitude} ${location.longitude}")
//            Toast.makeText(requireContext(), "Latitude: ${location.latitude} Longitude ${location.longitude}",
//                Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermissions(){
        println("requestPermissions")
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(
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
                println("onRequestPermissionsResult inside")
                getLastLocation()
            }
        }
//        else{
////            println("onRequestPermissionsResult inside")
//        }
        println("onRequestPermissionsResult")
    }

    private fun getWeather(){

        lifecycleScope.launch {

            val temperature: String = userPreferences.readTemperature()?: getString(R.string.celsius)
            var language: String = userPreferences.readLanguage()?: "en"

            var userLocation: String = userPreferences.readUserGPSLocation().toString()
            var latitude: Double = userPreferences.readGPSLatitude()!! // TODO Crash here in first time
            var longitude: Double = userPreferences.readGPSLongitude()!!

            if (userPreferences.readIsFavourite() == true){
                userLocation = userPreferences.readUserFavLocation().toString()
                latitude = userPreferences.readFavLatitude()!!
                longitude = userPreferences.readFavLongitude()!!
            } else {
                if(userPreferences.readLocation() == getString(R.string.map)){
                    userLocation = userPreferences.readUserMapLocation().toString()
                    if (userPreferences.readUserMapLocation().isNullOrEmpty() || userLocation == "null"){
                        if(userPreferences.readUserLastLocation().isNullOrEmpty() || userLocation == "null"){
                            userLocation = "Unknown"
                        }

                    }

                    latitude = userPreferences.readMapLatitude()?: userPreferences.readLastLatitude()!!
                    longitude = userPreferences.readMapLongitude()?: userPreferences.readLastLongitude()!!
                }
            }

            println("getWeather PREFS: isFav? ${userPreferences.readIsFavourite()} location: ${userPreferences.readLocation()}")
            println("getWeather PREFS Strings: location: $userLocation}")
            println("getWeather PREFS Strings: lat: $latitude long $longitude")

            if(userPreferences.readLanguage() == getString(R.string.arabic)){
                language = "ar"
            }

            val units = when(temperature){
                getString(R.string.celsius) -> getString(R.string.metric)
                getString(R.string.fahrenheit) -> getString(R.string.imperial)
                else -> ""
            }

            binding.location.text = userLocation

            if(isInternetAvailable(requireContext())) {
                viewModel.getWeather(latitude, longitude, units, language, API_KEY).observe(viewLifecycleOwner){ weather ->

                    if (weather != null){
                        dailyAdapter.setList(weather.daily)
                        hourlyAdapter.setList(weather.hourly)
                        dailyAdapter.notifyDataSetChanged()
                        hourlyAdapter.notifyDataSetChanged()
                        updateUI(weather, units)

                        val saveWeather = WeatherResponse(
                            location = userLocation,
                            current = weather.current,
                            daily = weather.daily,
                            hourly = weather.hourly,
                            lat = weather.lat,
                            lon = weather.lon,
                            timezone = weather.timezone,
                            timezone_offset = weather.timezone_offset
                        )
                        println("saveWeather: location: ${saveWeather.location}")
                        println("saveWeather: lat: ${saveWeather.lat} long: ${saveWeather.lon}")
                        viewModel.insertWeather(saveWeather)
                    }
                }
//                Toast.makeText(context, "Internet", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(context, "Offline", Toast.LENGTH_SHORT).show()
                viewModel.getWeatherFromLocation(userLocation).observe(viewLifecycleOwner) { weather ->
                    if(weather != null){
                        dailyAdapter.setList(weather.daily)
                        hourlyAdapter.setList(weather.hourly)
                        dailyAdapter.notifyDataSetChanged()
                        hourlyAdapter.notifyDataSetChanged()
                        updateUI(weather, units)
                    }
                }
            }
        }
    }

    private fun updateUI(weatherResponse: WeatherResponse, units: String){

        lifecycleScope.launch {

            var userLocation: String = userPreferences.readUserGPSLocation().toString()

            if (userPreferences.readIsFavourite() == true){
                userLocation = userPreferences.readUserFavLocation().toString()
            } else {
                if(userPreferences.readLocation() == getString(R.string.map)){
                    userLocation = userPreferences.readUserMapLocation().toString()
                }
            }

            binding.location.text = userLocation

        }

        val simpleDateFormat = SimpleDateFormat("EEEE dd/MM hh:mm a")
        val dateString = simpleDateFormat.format(weatherResponse.current.dt*1000L)
        binding.currentDate.text = String.format(dateString)

        binding.currentWeatherImage.load(weatherResponse.current.weather[0].icon)

        val currentWeather = when(units){
            getString(R.string.metric) -> weatherResponse.current.temp.toInt().toString() + "\u2103"
            getString(R.string.imperial) -> weatherResponse.current.temp.toInt().toString() + "\u2109"
            else -> weatherResponse.current.temp.toInt().toString() + "\u212A"
        }

        binding.currentWeather.text = currentWeather
        binding.feelsLike.text = "${getString(R.string.feels_like)} ${weatherResponse.current.feels_like}°"
        binding.currentWeatherDesc.text = weatherResponse.current.weather[0].description

        binding.currentPressure.text = "${weatherResponse.current.pressure} ${getString(R.string.hpa)}"
        binding.currentHumidity.text = weatherResponse.current.humidity.toString() + "%"
        binding.currentWind.text = "${weatherResponse.current.wind_speed} ${getString(R.string.m_per_s)}"
        binding.currentCloud.text = weatherResponse.current.clouds.toString() + "%"
        binding.currentUltraViolet.text = weatherResponse.current.uvi.toString()
        binding.currentVisibility.text = "${weatherResponse.current.visibility} ${getString(R.string.m)}"

    }

    private fun initRecyclerViews(){
        // daily
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        dailyAdapter = DailyAdapter(ArrayList())

        binding.dailyRecyclerView.adapter = dailyAdapter
        binding.dailyRecyclerView.layoutManager = layoutManager

        val topSpacingItemDecoration = TopSpacingItemDecoration(30)
        binding.dailyRecyclerView.removeItemDecoration(topSpacingItemDecoration)
        binding.dailyRecyclerView.addItemDecoration(topSpacingItemDecoration)

        // hourly
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        hourlyAdapter = HourlyAdapter(ArrayList())

        binding.hourlyRecyclerView.adapter = hourlyAdapter
        binding.hourlyRecyclerView.layoutManager = layoutManager

        val verticalSpacingItemDecoration = VerticalSpacingItemDecoration(10)
        binding.hourlyRecyclerView.removeItemDecoration(verticalSpacingItemDecoration)
        binding.hourlyRecyclerView.addItemDecoration(verticalSpacingItemDecoration)
    }

    override fun onStart() {
        super.onStart()
        if(!isLocationEnabled())
            enableLocationManager()
        println("onStart")
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
        getLastLocation()
    }

}