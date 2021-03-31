package com.taloslogy.playolapp.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel


class LoginViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val login: LiveData<Boolean> = MutableLiveData<Boolean>()
}