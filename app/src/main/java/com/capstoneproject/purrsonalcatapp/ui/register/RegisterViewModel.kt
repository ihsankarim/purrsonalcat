package com.capstoneproject.purrsonalcatapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstoneproject.purrsonalcatapp.data.remote.response.RegisterResponse
import com.capstoneproject.purrsonalcatapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = authRepository.registerAuth(username, email, password)
                _registerResult.value = response
            } catch (e: Exception) {
                Log.e("REGISTER ERROR", "An error occurred: ${e.message}")
                _registerResult.value =
                    RegisterResponse(error = true, message = "An error occurred.")
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}