package com.example.skycast.view.homeview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.data.repository.Repository

class HomeVmFactory (val repository: Repository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}