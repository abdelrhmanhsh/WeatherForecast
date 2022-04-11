package com.abdelrhmanhsh.weatherforecast.ui.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.model.response.Hourly
import com.abdelrhmanhsh.weatherforecast.util.Extensions.Companion.load
import java.text.SimpleDateFormat

class HourlyAdapter(
    private var hourlyList: List<Hourly>
): RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_weather_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.icon.load(hourlyList[position].weather[0].icon)

        val simpleDateFormat = SimpleDateFormat("hh a")
        val dateString = simpleDateFormat.format(hourlyList[position].dt*1000L)
        holder.time.text = String.format(dateString)

        holder.temperature.text = "${hourlyList[position].temp.toInt()}\u00B0"
    }

    fun setList(hourlyList: List<Hourly>){
        this.hourlyList = hourlyList
    }

    override fun getItemCount(): Int {
        return hourlyList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val temperature: TextView
            get() = itemView.findViewById(R.id.temperature)

        val icon: ImageView
            get() = itemView.findViewById(R.id.icon)

        val time: TextView
            get() = itemView.findViewById(R.id.time)
    }

}