package ru.netology.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.model.AuthModel
import ru.netology.nework.model.PhotoModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {

    val data: LiveData<AuthModel> = appAuth.authState.asLiveData(Dispatchers.Default)

    private val _photoData: MutableLiveData<PhotoModel?> = MutableLiveData(null)
    val photoData: LiveData<PhotoModel?>
        get() = _photoData

    fun register(login: String, name: String, pass: String) {
        viewModelScope.launch {
            val photo = _photoData.value
            appAuth.register(login, name, pass, photo)
        }
    }

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            appAuth.login(login, pass)
        }
    }

    fun setPhoto(uri: Uri, file: File) {
        _photoData.value = PhotoModel(uri, file)
    }
}