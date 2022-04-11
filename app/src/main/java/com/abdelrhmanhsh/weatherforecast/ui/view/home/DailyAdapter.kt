package com.abdelrhmanhsh.weatherforecast.ui.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.model.response.Daily
import com.abdelrhmanhsh.weatherforecast.util.Extensions.Companion.load
import java.text.SimpleDateFormat

class DailyAdapter(
    private var dailyList: List<Daily>
    ): RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daily_weather_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.icon.load(dailyList[position].weather[0].icon)

        val simpleDateFormat = SimpleDateFormat("EEEE dd/MM")
        val dateString = simpleDateFormat.format(dailyList[position].dt*1000L)
        holder.day.text = String.format(dateString)

        holder.description.text = dailyList[position].weather[0].description
        holder.maxTemp.text = "${dailyList[position].temp.max.toInt()}\u00B0"
        holder.minTemp.text = "${dailyList[position].temp.min.toInt()}\u00B0"
    }

    fun setList(dailyList: List<Daily>){
        this.dailyList = dailyList
    }

    override fun getItemCount(): Int {
        return dailyList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val day: TextView
            get() = itemView.findViewById(R.id.day)

        val description: TextView
            get() = itemView.findViewById(R.id.description)

        val icon: ImageView
            get() = itemView.findViewById(R.id.daily_icon)

        val maxTemp: TextView
            get() = itemView.findViewById(R.id.max_temp)

        val minTemp: TextView
            get() = itemView.findViewById(R.id.min_temp)
    }

}