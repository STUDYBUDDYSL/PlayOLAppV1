package com.taloslogy.playolapp.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taloslogy.playolapp.utils.storage.UserKeyPreference

class UserViewModelFactory(private val userKeyPreference: UserKeyPreference) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userKeyPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}