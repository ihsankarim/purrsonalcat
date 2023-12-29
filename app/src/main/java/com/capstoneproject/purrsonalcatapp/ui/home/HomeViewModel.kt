package com.capstoneproject.purrsonalcatapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstoneproject.purrsonalcatapp.data.remote.response.UserResponse
import com.capstoneproject.purrsonalcatapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _userData = MutableStateFlow<UserResponse?>(null)
    val userData = _userData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    init {
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.getUserData().collect { result ->
                _userData.value = result
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}