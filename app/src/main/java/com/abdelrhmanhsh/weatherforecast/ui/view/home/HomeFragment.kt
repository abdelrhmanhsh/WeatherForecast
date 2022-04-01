package com.abdelrhmanhsh.weatherforecast.ui.view.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.databinding.FragmentHomeBinding
import com.abdelrhmanhsh.weatherforecast.model.Repository
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.home.HomeViewModel
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.home.HomeViewModelFactory
import com.abdelrhmanhsh.weatherforecast.util.Constants
import com.abdelrhmanhsh.weatherforecast.util.Extensions.Companion.load
import com.abdelrhmanhsh.weatherforecast.util.PrivateConstants.Companion.API_KEY
import com.abdelrhmanhsh.weatherforecast.util.UserPreferences
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class HomeFragment : Fragment() {

    val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(context!!)
        viewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                context!!,
                WeatherClient.getInstance()!!
            )!!
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        getWeather()

        initRecyclerViews()

    }

    private fun getWeather(){

        lifecycleScope.launch {

            var userLocation: String = userPreferences.readUserGPSLocation().toString()
            var latitude: Double = userPreferences.readGPSLatitude()!!
            var longitude: Double = userPreferences.readGPSLatitude()!!
            val temperature: String = userPreferences.readTemperature()?: getString(R.string.celsius)
            var language: String = userPreferences.readLanguage()?: "en"

            if(userPreferences.readLocation() == getString(R.string.map)){

                userLocation = userPreferences.readUserMapLocation().toString()
                latitude = userPreferences.readMapLatitude()!!
                longitude = userPreferences.readMapLatitude()!!

            }

            if(userPreferences.readLanguage() == getString(R.string.arabic)){
                language = "ar"
            }

//            val units: String
            val units = when(temperature){
                getString(R.string.celsius) -> getString(R.string.metric)
                getString(R.string.fahrenheit) -> getString(R.string.imperial)
                else -> ""
            }

            binding.location.text = userLocation

            viewModel.getWeather(latitude, longitude, units, language, API_KEY).observe(this@HomeFragment){ weather ->
//                Log.i(TAG, "onCreate: movies: $weather")
                if (weather != null){
                    dailyAdapter.setList(weather.daily)
                    hourlyAdapter.setList(weather.hourly)
                    dailyAdapter.notifyDataSetChanged()
                    hourlyAdapter.notifyDataSetChanged()
                    updateUI(weather, units)
                }
            }
        }
    }

    private fun updateUI(weatherResponse: WeatherResponse, units: String){

        lifecycleScope.launch {

            val userLocation: String

            if(userPreferences.readLocation().isNullOrEmpty() || userPreferences.readLocation() == getString(R.string.gps)){
                userLocation = userPreferences.readUserGPSLocation().toString()
                println("USER CURRENT LOCATION GPS ${userPreferences.readUserGPSLocation().toString()}")
            } else {
                userLocation = userPreferences.readUserMapLocation().toString()
                println("USER CURRENT LOCATION MAP ${userPreferences.readUserMapLocation().toString()}")
            }
            binding.location.text = userLocation

        }

        val simpleDateFormat = SimpleDateFormat("EEEE dd/MM hh:mm a")
        val dateString = simpleDateFormat.format(weatherResponse.current.dt*1000L)
        binding.currentDate.text = String.format(dateString)

        binding.currentWeatherImage.load(weatherResponse.current.weather[0].icon)


        val currentWeather = when(units){
            getString(R.string.metric) -> weatherResponse.current.temp.toString() + "\u2103"
            getString(R.string.imperial) -> weatherResponse.current.temp.toString() + "\u2109"
            else -> weatherResponse.current.temp.toString() + "\u212A"
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

        // hourly
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        hourlyAdapter = HourlyAdapter(ArrayList())

        binding.hourlyRecyclerView.adapter = hourlyAdapter
        binding.hourlyRecyclerView.layoutManager = layoutManager
    }

}