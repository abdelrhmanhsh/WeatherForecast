package com.abdelrhmanhsh.weatherforecast.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abdelrhmanhsh.weatherforecast.databinding.FragmentAlertsBinding

class AlertsFragment : Fragment() {

    private lateinit var binding: FragmentAlertsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

}