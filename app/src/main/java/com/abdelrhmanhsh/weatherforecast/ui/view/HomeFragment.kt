package com.abdelrhmanhsh.weatherforecast.ui.view

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
import com.abdelrhmanhsh.weatherforecast.databinding.FragmentAlertsBinding
import com.abdelrhmanhsh.weatherforecast.databinding.FragmentHomeBinding
import com.abdelrhmanhsh.weatherforecast.model.Repository
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.WeatherViewModel
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.WeatherViewModelFactory
import com.abdelrhmanhsh.weatherforecast.util.PrivateConstants.Companion.API_KEY
import com.abdelrhmanhsh.weatherforecast.util.UserPreferences
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: WeatherViewModel
    private lateinit var viewModelFactory: WeatherViewModelFactory
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
        viewModelFactory = WeatherViewModelFactory(
            Repository.getInstance(
                context!!,
                WeatherClient.getInstance()!!
            )!!
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherViewModel::class.java)
        viewModel.getWeather(31.2149417, 29.924295, "minutely", API_KEY).observe(this){ weather ->
            Log.i(TAG, "onCreate: movies: $weather")
            if (weather != null){
                dailyAdapter.setList(weather.daily)
                hourlyAdapter.setList(weather.hourly)
                dailyAdapter.notifyDataSetChanged()
                hourlyAdapter.notifyDataSetChanged()
                updateUI(weather)
            }
        }

        initRecyclerViews()

    }

    private fun updateUI(weatherResponse: WeatherResponse){

        lifecycleScope.launch {
            val userLocation = userPreferences.readUserLocation()
            binding.location.text = userLocation
        }

        binding.currentWeather.text = weatherResponse.current.temp.toString()
        binding.feelsLike.text = "${getString(R.string.feels_like)} ${weatherResponse.current.feels_like}"
        binding.currentWeatherDesc.text = weatherResponse.current.weather[0].description

        binding.currentPressure.text = weatherResponse.current.pressure.toString() + " hpa"
        binding.currentHumidity.text = weatherResponse.current.humidity.toString() + "%"
        binding.currentWind.text = weatherResponse.current.wind_speed.toString() + " m/s"
        binding.currentCloud.text = weatherResponse.current.clouds.toString() + "%"
        binding.currentUltraViolet.text = weatherResponse.current.uvi.toString()
        binding.currentVisibility.text = weatherResponse.current.visibility.toString() + " m"

    }

    private fun initRecyclerViews(){
        // daily
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        dailyAdapter = DailyAdapter(context!!, ArrayList())

        binding.dailyRecyclerView.adapter = dailyAdapter
        binding.dailyRecyclerView.layoutManager = layoutManager

        // hourly
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        hourlyAdapter = HourlyAdapter(context!!, ArrayList())

        binding.hourlyRecyclerView.adapter = hourlyAdapter
        binding.hourlyRecyclerView.layoutManager = layoutManager
//        binding.hourlyRecyclerView.apply {
//            adapter = hourlyAdapter
//            layoutManager = layoutManager
//        }
    }

}