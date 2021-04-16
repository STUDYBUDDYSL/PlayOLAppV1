package com.taloslogy.playolapp.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taloslogy.playolapp.utils.storage.PrefHelper

class UserViewModelFactory(private val prefs: PrefHelper) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}