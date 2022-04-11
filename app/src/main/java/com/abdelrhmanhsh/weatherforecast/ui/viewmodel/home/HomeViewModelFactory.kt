package com.abdelrhmanhsh.weatherforecast.ui.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrhmanhsh.weatherforecast.model.RepositoryInterface
import java.lang.IllegalArgumentException

class HomeViewModelFactory(private val repositoryInterface: RepositoryInterface): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repositoryInterface) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }

}