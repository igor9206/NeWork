package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {

    val data: LiveData<AuthState> = appAuth.authState.asLiveData(Dispatchers.Default)

    fun register(login: String, name: String, pass: String) {
        viewModelScope.launch {
            appAuth.register(login, name, pass)
        }
    }

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            appAuth.login(login, pass)
        }
    }
}