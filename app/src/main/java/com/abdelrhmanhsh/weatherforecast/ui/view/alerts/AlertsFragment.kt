package com.abdelrhmanhsh.weatherforecast.ui.view.alerts

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.databinding.AddAlertDialogBinding
import com.abdelrhmanhsh.weatherforecast.databinding.FragmentAlertsBinding
import com.abdelrhmanhsh.weatherforecast.db.ConcreteLocalSource
import com.abdelrhmanhsh.weatherforecast.model.response.Alert
import com.abdelrhmanhsh.weatherforecast.model.Repository
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.alerts.AlertsViewModel
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.alerts.AlertsViewModelFactory
import com.abdelrhmanhsh.weatherforecast.util.*
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_ALERT_ID
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_END_MILLIS
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LANGUAGE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LATITUDE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_LONGITUDE
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_START_MILLIS
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.DATA_UNITS
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AlertsFragment : Fragment(), View.OnClickListener {

    private val START_DATE = 1
    private val END_DATE = 2

    private lateinit var binding: FragmentAlertsBinding
    private lateinit var dialogBinding: AddAlertDialogBinding
    private lateinit var viewModel: AlertsViewModel
    private lateinit var viewModelFactory: AlertsViewModelFactory
    private lateinit var adapter: AlertsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var userPreferences: UserPreferences

    private var startMillisAlert: Long = 0
    private var endMillisAlert: Long = 0
    private var startDateAlert: String = ""
    private var endDateAlert: String = ""
    private var startTimeAlert: String = ""
    private var endTimeAlert: String = ""
    private var language: String = ""
    private var units: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingAddAlert.setOnClickListener(this)
        userPreferences = UserPreferences(requireContext())

        lifecycleScope.launch {
            language = userPreferences.readLanguage()?: "en"
            units = userPreferences.readTemperature()?: "metric"
        }

        viewModelFactory = AlertsViewModelFactory(
            Repository.getInstance(
                requireContext(),
                WeatherClient.getInstance()!!,
                ConcreteLocalSource(requireContext())
            )!!
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(AlertsViewModel::class.java)
        initRecyclerView()
        getAlerts()

    }

    private fun getAlerts(){
        viewModel.getAlerts().observe(viewLifecycleOwner){ alerts ->
            adapter.setList(alerts)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView(){
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        adapter = AlertsAdapter(ArrayList()) { deleteAlert ->
            deleteAlert(deleteAlert)
        }

        binding.alertsRecyclerView.adapter = adapter
        binding.alertsRecyclerView.layoutManager = layoutManager

        val topSpacingItemDecoration = TopSpacingItemDecoration(30)
        binding.alertsRecyclerView.removeItemDecoration(topSpacingItemDecoration)
        binding.alertsRecyclerView.addItemDecoration(topSpacingItemDecoration)

    }

    private fun deleteAlert(alert: Alert){
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(getString(R.string.are_you_sure))
            setMessage(getString(R.string.delete_alert_message))
            setPositiveButton(getString(R.string.delete)) { dialog, which ->

                viewModel.deleteAlert(alert)
                WorkManager.getInstance(requireContext()).cancelAllWorkByTag(alert.id.toString())

                Snackbar.make(binding.alertsRecyclerView, getString(R.string.alert_deleted), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) {

                        try {
                            viewModel.addAlert(alert)
                            setAlertWorker(alert.id, alert.startDateMillis, alert.endDateMillis, alert.latitude, alert.longitude)
                        } catch (e: IllegalStateException) {
                            println("couldn't delete")
                        }

                    }.show()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, which ->

            }
            show()
        }
    }

    private fun showAddAlertDialog(){

        val dialog = Dialog(requireContext())
        dialogBinding = AddAlertDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        startMillisAlert = System.currentTimeMillis() + 120_000 // set start time to two minutes later from now
        endMillisAlert = System.currentTimeMillis() + 86_520_000 // set end date to one day later from today

        val dateFormatter = SimpleDateFormat("dd MMM")
        startDateAlert = dateFormatter.format(Date(startMillisAlert))
        endDateAlert = dateFormatter.format(Date(endMillisAlert))

        val timeFormatter = SimpleDateFormat("HH:mm")
        startTimeAlert = timeFormatter.format(Date(startMillisAlert))
        endTimeAlert = timeFormatter.format(Date(endMillisAlert))

        dialogBinding.startDate.text = startDateAlert
        dialogBinding.endDate.text = endDateAlert
        dialogBinding.startTime.text = startTimeAlert
        dialogBinding.endTime.text = endTimeAlert

        // get last lat long to set alert with it
        var latitude = 0.0
        var longitude = 0.0
        lifecycleScope.launch {
//            latitude = userPreferences.readLastLatitude()!!
//            longitude = userPreferences.readLastLongitude()!!

            latitude = userPreferences.readGPSLatitude()!!
            longitude = userPreferences.readGPSLongitude()!!

            if (userPreferences.readIsFavourite() == true){
                latitude = userPreferences.readFavLatitude()!!
                longitude = userPreferences.readFavLongitude()!!
            } else {
                if(userPreferences.readLocation() == getString(R.string.map)){
                    latitude = userPreferences.readMapLatitude()!!
                    longitude = userPreferences.readMapLongitude()!!
                }
            }

        }

        dialogBinding.linearFrom.setOnClickListener {
            showDatePicker(START_DATE)
        }

        dialogBinding.linearTo.setOnClickListener {
            showDatePicker(END_DATE)
        }

        dialogBinding.btnSaveAlert.setOnClickListener {

            if(startMillisAlert <= System.currentTimeMillis()) {

                Toast.makeText(context, getString(R.string.provide_time_in_future_alert), Toast.LENGTH_SHORT).show()

            } else if(startMillisAlert > endMillisAlert){

                Toast.makeText(context, getString(R.string.provide_valid_date_alert), Toast.LENGTH_SHORT).show()

            } else {

                saveAlert(
                    Alert(
                        id = System.currentTimeMillis(),
                        startDate = startDateAlert,
                        endDate = endDateAlert,
                        startTime = startTimeAlert,
                        endTime = endTimeAlert,
                        startDateMillis = startMillisAlert,
                        endDateMillis = endMillisAlert,
                        latitude = latitude,
                        longitude = longitude
                    )
                )
                dialog.dismiss()

            }
        }

        dialog.show()

        val window: Window? = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    }

    private fun saveAlert(alert: Alert){
        viewModel.addAlert(alert)
        setAlertWorker(alert.id, alert.startDateMillis, alert.endDateMillis, alert.latitude, alert.longitude)
    }

    private fun setAlertWorker(id: Long, startMillis: Long, endMillis: Long, latitude: Double, longitude: Double){

        val data = Data.Builder()
            .putLong(DATA_ALERT_ID, id)
            .putDouble(DATA_LATITUDE, latitude)
            .putDouble(DATA_LONGITUDE, longitude)
            .putString(DATA_LANGUAGE, language)
            .putString(DATA_UNITS, units)
            .putLong(DATA_START_MILLIS, startMillis)
            .putLong(DATA_END_MILLIS, endMillis)
            .build()

        val currentMillis = System.currentTimeMillis()
        val diffMillis = startMillis - currentMillis

        val networkConstraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val coroutinesWorker = OneTimeWorkRequestBuilder<AlertWorker>()
            .setInputData(data)
            .setConstraints(networkConstraint)
            .setInitialDelay(diffMillis, TimeUnit.MILLISECONDS)
            .addTag(id.toString())
            .build()

        WorkManager.getInstance(requireContext()).enqueue(coroutinesWorker)
    }

    private fun showDatePicker(flag: Int){
        val calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { view, schedYear, schedMonth, schedDay ->

            timePickerDialog(schedYear, schedMonth, schedDay, flag)

        }, year, month, day)

        datePickerDialog.show()
    }

    fun getEquivalentMonth(month: Int): String {
        return when (month) {
            0 -> getString(R.string.jan)
            1 -> getString(R.string.feb)
            2 -> getString(R.string.mar)
            3 -> getString(R.string.apr)
            4 -> getString(R.string.may)
            5 -> getString(R.string.jun)
            6 -> getString(R.string.jul)
            7 -> getString(R.string.aug)
            8 -> getString(R.string.sep)
            9 -> getString(R.string.oct)
            10 -> getString(R.string.nov)
            11 -> getString(R.string.dec)
            else -> "Err"
        }
    }

    private fun timePickerDialog(schedYear: Int, schedMonth: Int, schedDay: Int, flag: Int){

        val calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE) + 2

        val timePickerDialog = TimePickerDialog(requireContext(), { view, schedHour, schedMinute ->

            val fullDate = "$schedDay-${schedMonth+1}-$schedYear/$schedHour:$schedMinute"
            val sdf = SimpleDateFormat("dd-MM-yyyy/hh:mm")
            val dateStr = "$schedDay ${getEquivalentMonth(schedMonth)} $schedYear"



//            var a = "PM"
//            if(schedHour < 12)
//                a = "AM"

//            val timeStr = "$schedHour:$schedMinute $a"
            val timeStr = "$schedHour:$schedMinute"

            try {
                val date = sdf.parse(fullDate)
                calendar.time = date
                if (flag == START_DATE){
                    startMillisAlert = calendar.timeInMillis
                    startDateAlert = dateStr
                    startTimeAlert = timeStr
                    dialogBinding.startDate.text = dateStr
                    dialogBinding.startTime.text = timeStr
                } else {
                    endMillisAlert = calendar.timeInMillis
                    endDateAlert = dateStr
                    endTimeAlert = timeStr
                    dialogBinding.endDate.text = dateStr
                    dialogBinding.endTime.text = timeStr
                }

            } catch (e: ParseException){
                e.printStackTrace()
            }

        }, hour, minute, false)

        timePickerDialog.show()

    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.floating_add_alert -> showAddAlertDialog()
        }
    }

}