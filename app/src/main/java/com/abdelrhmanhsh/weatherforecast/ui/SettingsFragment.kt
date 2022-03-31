package com.abdelrhmanhsh.weatherforecast.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.databinding.FragmentSettingsBinding
import com.abdelrhmanhsh.weatherforecast.util.UserPreferences
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(), View.OnClickListener{

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences(context!!)
        settingRadioButtons()

        binding.radioGps.setOnClickListener(this)
        binding.radioMap.setOnClickListener(this)
        binding.radioEnglish.setOnClickListener(this)
        binding.radioArabic.setOnClickListener(this)
        binding.radioCelsius.setOnClickListener(this)
        binding.radioKelvin.setOnClickListener(this)
        binding.radioFahrenheit.setOnClickListener(this)
        binding.radioMeterPerSecond.setOnClickListener(this)
        binding.radioMilePerHour.setOnClickListener(this)

    }

    private fun settingRadioButtons() {
        lifecycleScope.launch {
            val locationSelection = userPreferences.readLocation()
            val languageSelection = userPreferences.readLanguage()
            val temperatureSelection = userPreferences.readTemperature()
            val windSpeedSelection = userPreferences.readWindSpeed()

            println("PREFS: $locationSelection $languageSelection $temperatureSelection $windSpeedSelection")

            // setting location
            when(locationSelection){
                getString(R.string.gps) -> binding.radioGps.isChecked = true
                getString(R.string.map) -> binding.radioMap.isChecked = true
                else -> binding.radioGps.isChecked = true
            }

            // setting language
            when(languageSelection){
                getString(R.string.english) -> binding.radioEnglish.isChecked = true
                getString(R.string.arabic) -> binding.radioArabic.isChecked = true
                else -> binding.radioEnglish.isChecked = true
            }

            // setting temperature
            when(temperatureSelection){
                getString(R.string.celsius) -> binding.radioCelsius.isChecked = true
                getString(R.string.kelvin) -> binding.radioKelvin.isChecked = true
                getString(R.string.fahrenheit) -> binding.radioFahrenheit.isChecked = true
                else -> binding.radioCelsius.isChecked = true
            }

            // setting wind speed
            when(windSpeedSelection){
                getString(R.string.meter_per_sec) -> binding.radioMeterPerSecond.isChecked = true
                getString(R.string.mile_per_hour) -> binding.radioMilePerHour.isChecked = true
                else -> binding.radioMeterPerSecond.isChecked = true
            }

        }
    }

    private fun selectionLocation(selection: String, view: View){
        lifecycleScope.launch {
            userPreferences.storeLocationPref(selection)
        }
        if(selection == getString(R.string.map)){
            val action = SettingsFragmentDirections.actionSettingsFragmentToMapsFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    private fun selectionLanguage(selection: String){
        lifecycleScope.launch {
            userPreferences.storeLanguagePref(selection)
        }
    }

    private fun selectionTemperature(selection: String){
        lifecycleScope.launch {
            userPreferences.storeTemperaturePref(selection)
        }
    }

    private fun selectionWindSpeed(selection: String){
        lifecycleScope.launch {
            userPreferences.storeWindSpeedPref(selection)
        }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.radio_gps -> selectionLocation(getString(R.string.gps), view)
            R.id.radio_map -> selectionLocation(getString(R.string.map), view)
            R.id.radio_english -> selectionLanguage(getString(R.string.english))
            R.id.radio_arabic -> selectionLanguage(getString(R.string.arabic))
            R.id.radio_celsius -> selectionTemperature(getString(R.string.celsius))
            R.id.radio_kelvin -> selectionTemperature(getString(R.string.kelvin))
            R.id.radio_fahrenheit -> selectionTemperature(getString(R.string.fahrenheit))
            R.id.radio_meter_per_second -> selectionWindSpeed(getString(R.string.meter_per_sec))
            R.id.radio_mile_per_hour -> selectionWindSpeed(getString(R.string.mile_per_hour))
        }
    }
}