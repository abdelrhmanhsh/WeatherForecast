package com.abdelrhmanhsh.weatherforecast.ui.viewmodel.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrhmanhsh.weatherforecast.model.RepositoryInterface
import java.lang.IllegalArgumentException

class FavouritesViewModelFactory(private val repositoryInterface: RepositoryInterface): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavouritesViewModel::class.java)){
            FavouritesViewModel(repositoryInterface) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }

}