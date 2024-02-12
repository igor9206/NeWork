package ru.netology.nework.auth

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.R
import ru.netology.nework.api.ApiService
import ru.netology.nework.model.PhotoModel
import javax.inject.Inject
import javax.inject.Singleton

data class AuthState(
    val id: Long = 0L,
    val token: String? = null
)

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val apiService: ApiService
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private val idKey = "id"
    private val tokenKey = "token"
    private val _authState = MutableStateFlow(
        AuthState(
            prefs.getLong(idKey, 0L),
            prefs.getString(tokenKey, null)
        )
    )

    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authState.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            commit()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authState.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }

    suspend fun register(login: String, name: String, pass: String, photo: PhotoModel?) {
        try {
            val response = if (photo != null) {
                val part = MultipartBody.Part.createFormData(
                    "file",
                    photo.file.name,
                    photo.file.asRequestBody()
                )
                apiService.usersRegistrationWithPhoto(login, pass, name, part)
            } else {
                apiService.usersRegistration(login, pass, name)
            }

            if (!response.isSuccessful) {
                when (response.code()) {
                    403 -> {
                        toastMsg(context.getString(R.string.the_user_is_already_registered))
                    }

                    415 -> {
                        toastMsg(context.getString(R.string.incorrect_photo_format))
                    }

                    else -> toastMsg("${context.getString(R.string.unknown_error)}: ${response.code()}")
                }
                return
            }

            val body = response.body() ?: throw Exception("Body is Empty")
            setAuth(body.id, body.token)
        } catch (e: Exception) {
            toastMsg("${context.getString(R.string.unknown_error)}: ${e.message}")
        }
    }

    suspend fun login(login: String, pass: String) {
        try {
            val response = apiService.usersAuthentication(login, pass)
            if (!response.isSuccessful) {
                when (response.code()) {
                    400 -> {
                        toastMsg(context.getString(R.string.incorrect_password))
                    }

                    404 -> {
                        toastMsg(context.getString(R.string.user_unregistered))
                    }

                    else -> toastMsg("${context.getString(R.string.unknown_error)}: ${response.code()}")
                }
                return
            }

            val body = response.body() ?: throw Exception("Body is Empty")
            setAuth(body.id, body.token)
        } catch (e: Exception) {
            toastMsg("${context.getString(R.string.unknown_error)}: ${e.message}")
        }
    }

    private fun toastMsg(msg: String) {
        Toast.makeText(
            context,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

}