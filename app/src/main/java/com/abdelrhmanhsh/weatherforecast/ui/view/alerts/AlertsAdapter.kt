package com.abdelrhmanhsh.weatherforecast.ui.view.alerts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.model.response.Alert

class AlertsAdapter(
    private var alerts: List<Alert>,
    private var deleteListener: (Alert) -> Unit

): RecyclerView.Adapter<AlertsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alert_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.alertStartDate.text = alerts[position].startDate
        holder.alertStartTime.text = alerts[position].startTime
        holder.alertEndDate.text = alerts[position].endDate
        holder.alertEndTime.text = alerts[position].endTime

        holder.imageDelete.setOnClickListener {
            deleteListener(alerts[position])
        }
    }

    fun setList(alerts: List<Alert>){
        this.alerts = alerts
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val alertStartDate: TextView
            get() = itemView.findViewById(R.id.alert_start_date)

        val alertStartTime: TextView
            get() = itemView.findViewById(R.id.alert_start_time)

        val alertEndDate: TextView
            get() = itemView.findViewById(R.id.alert_end_date)

        val alertEndTime: TextView
            get() = itemView.findViewById(R.id.alert_end_time)

        val imageDelete: ImageView
            get() = itemView.findViewById(R.id.image_delete_alert)

    }

}