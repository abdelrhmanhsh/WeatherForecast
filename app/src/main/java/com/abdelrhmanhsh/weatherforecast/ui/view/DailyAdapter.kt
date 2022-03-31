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

class DailyAdapter(
    private val context: Context,
    private var dailyList: List<Daily>
    ): RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daily_weather_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyAdapter.ViewHolder, position: Int) {

//        Glide.with(context)
//            .load(dailyList[position].image)
//            .apply(
//                RequestOptions()
//                .placeholder(R.drawable.ic_launcher_background)
//                .error(R.drawable.ic_launcher_background))
//            .into(holder.image)

//        holder.day.text = dailyList[position].title
        holder.description.text = dailyList[position].weather[0].description
        holder.maxTemp.text = dailyList[position].temp.max.toString()
        holder.minTemp.text = dailyList[position].temp.min.toString()
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
            get() = itemView.findViewById(R.id.icon)

        val maxTemp: TextView
            get() = itemView.findViewById(R.id.max_temp)

        val minTemp: TextView
            get() = itemView.findViewById(R.id.min_temp)
    }

}