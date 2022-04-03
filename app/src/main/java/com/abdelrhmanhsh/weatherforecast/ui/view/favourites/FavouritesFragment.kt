package com.abdelrhmanhsh.weatherforecast.ui.view.favourites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdelrhmanhsh.weatherforecast.R
import com.abdelrhmanhsh.weatherforecast.databinding.FragmentFavouritesBinding
import com.abdelrhmanhsh.weatherforecast.db.ConcreteLocalSource
import com.abdelrhmanhsh.weatherforecast.model.Repository
import com.abdelrhmanhsh.weatherforecast.model.response.FavouriteWeather
import com.abdelrhmanhsh.weatherforecast.model.response.WeatherResponse
import com.abdelrhmanhsh.weatherforecast.network.WeatherClient
import com.abdelrhmanhsh.weatherforecast.ui.view.MapsFragmentDirections
import com.abdelrhmanhsh.weatherforecast.ui.view.settings.SettingsFragmentDirections
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.favourites.FavouritesViewModel
import com.abdelrhmanhsh.weatherforecast.ui.viewmodel.favourites.FavouritesViewModelFactory
import com.abdelrhmanhsh.weatherforecast.util.Constants.Companion.MAPS_FROM_FAVOURITES
import com.abdelrhmanhsh.weatherforecast.util.TopSpacingItemDecoration
import com.abdelrhmanhsh.weatherforecast.util.UserPreferences
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavouritesFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentFavouritesBinding

    private lateinit var viewModel: FavouritesViewModel
    private lateinit var viewModelFactory: FavouritesViewModelFactory
    private lateinit var adapter: FavouritesAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingAddFavourite.setOnClickListener(this)
        userPreferences = UserPreferences(context!!)

        viewModelFactory = FavouritesViewModelFactory(
            Repository.getInstance(
                context!!,
                WeatherClient.getInstance()!!,
                ConcreteLocalSource(context!!)
            )!!
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(FavouritesViewModel::class.java)
        initRecyclerView()
        getFavourites()
    }

    private fun getFavourites(){
        viewModel.getFavourites().observe(this){ favourites ->
            adapter.setList(favourites)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView(){
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        adapter = FavouritesAdapter(ArrayList(),{ deleteWeather ->

            deleteWeather(deleteWeather)
        }, { getWeather ->
            getFavWeather(getWeather)
        })

        binding.favouriteRecyclerView.adapter = adapter
        binding.favouriteRecyclerView.layoutManager = layoutManager

        val topSpacingItemDecoration = TopSpacingItemDecoration(30)
        binding.favouriteRecyclerView.removeItemDecoration(topSpacingItemDecoration)
        binding.favouriteRecyclerView.addItemDecoration(topSpacingItemDecoration)

    }

    private fun getFavWeather(favouriteWeather: FavouriteWeather){
        lifecycleScope.launch {
            userPreferences.storeUserFavLocationPref(favouriteWeather.location)
            userPreferences.storeFavLongLatPref(favouriteWeather.lat, favouriteWeather.lon)
            userPreferences.storeIsFavouritePref(true)

            println("fav weather: location: ${favouriteWeather.location}")
            println("fav weather: lat: ${favouriteWeather.lat} long: ${favouriteWeather.lon}")
        }

        val action = FavouritesFragmentDirections.actionFavouritesToHome()
        Navigation.findNavController(view!!).navigate(action)

    }

    private fun deleteWeather(favouriteWeather: FavouriteWeather){
        val builder = AlertDialog.Builder(context!!)
        builder.apply {
            setTitle(favouriteWeather.location)
            setMessage("${getString(R.string.are_you_sure_delete1)} ${favouriteWeather.location} ${getString(R.string.are_you_sure_delete2)}")
            setPositiveButton(getString(R.string.delete)) { dialog, which ->

                viewModel.deleteLocationFromFavourites(favouriteWeather)
                Snackbar.make(binding.favouriteRecyclerView, getString(R.string.location_deleted), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) {
                        viewModel.addWeatherToFavourites(favouriteWeather)
                    }.show()

            }
            setNegativeButton(getString(R.string.cancel)) { dialog, which ->

            }
            show()
        }
    }

    private fun goToMap(view: View){
        val action: FavouritesFragmentDirections.ActionFavouritesToMaps
            = FavouritesFragmentDirections.actionFavouritesToMaps()
        action.flag = MAPS_FROM_FAVOURITES
        Navigation.findNavController(view).navigate(action)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.floating_add_favourite -> goToMap(view)
        }
    }
}