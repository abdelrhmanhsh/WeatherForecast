package com.abdelrhmanhsh.weatherforecast.ui.view.favourites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather

class FavouritesAdapter(
    private var favourites: List<FavouriteWeather>,
    private var deleteListener: (FavouriteWeather) -> Unit,
    private var listener: (FavouriteWeather) -> Unit

): RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.favouriteCountryName.text = favourites[position].location

        holder.imageDelete.setOnClickListener {
            deleteListener(favourites[position])
        }

        holder.layout.setOnClickListener {
            listener(favourites[position])
        }

    }

    fun setList(favourites: List<FavouriteWeather>){
        this.favourites = favourites
    }

    override fun getItemCount(): Int {
        return favourites.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val favouriteCountryName: TextView
            get() = itemView.findViewById(R.id.favourite_country_name)

        val imageDelete: ImageView
            get() = itemView.findViewById(R.id.image_delete_favourite)

        val layout: ConstraintLayout
            get() = itemView.findViewById(R.id.favourite_item_layout)
    }

}