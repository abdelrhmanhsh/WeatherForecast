package com.abdelrhmanhsh.weatherforecast.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.model.response.Daily
import com.abdelrhmanhsh.weatherforecast.model.response.Hourly

class HourlyAdapter(
    private val context: Context,
    private var hourlyList: List<Hourly>
): RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_weather_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyAdapter.ViewHolder, position: Int) {

//        Glide.with(context)
//            .load(dailyList[position].image)
//            .apply(
//                RequestOptions()
//                .placeholder(R.drawable.ic_launcher_background)
//                .error(R.drawable.ic_launcher_background))
//            .into(holder.image)

        holder.temperature.text = hourlyList[position].temp.toString()
//        holder.time.text = hourlyList[position].temp.toString()
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