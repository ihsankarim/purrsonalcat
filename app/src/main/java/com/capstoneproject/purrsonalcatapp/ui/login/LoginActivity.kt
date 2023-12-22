package com.capstoneproject.purrsonalcatapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstoneproject.purrsonalcatapp.data.local.pref.AuthPreferences
import com.capstoneproject.purrsonalcatapp.data.local.pref.dataStore
import com.capstoneproject.purrsonalcatapp.data.remote.retrofit.ApiConfig
import com.capstoneproject.purrsonalcatapp.data.repository.AuthRepository
import com.capstoneproject.purrsonalcatapp.databinding.ActivityLoginBinding
import com.capstoneproject.purrsonalcatapp.ui.AuthViewModelFactory
import com.capstoneproject.purrsonalcatapp.ui.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import android.widget.*
import com.capstoneproject.purrsonalcatapp.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authPreferences = AuthPreferences.getInstance(this.dataStore)
        setupLogin()

    }

    private fun setupLogin() {
        val token = runBlocking { authPreferences.authToken.first() }
        val apiService = ApiConfig.getApiService(token)
        val repository = AuthRepository(apiService, authPreferences)
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repository, authPreferences)).get(
            LoginViewModel::class.java
        )

        binding.loginButton.setOnClickListener {
            try {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                viewModel.login(email, password)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Login Exception", "Exception during login: ${e.message}")
                showToast("Login Failed: Invalid data, Please check your input.")
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        viewModel.loginResult.observe(this) { response ->
            if (!response.error!!) {
                val token = response.data?.token
                val username = response.data?.username
                lifecycleScope.launch {
                    authPreferences.saveAuthToken(token)
                    authPreferences.authToken.collect { storedToken ->
                        if (!storedToken.isNullOrBlank()) {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra("USERNAME", username)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("token empty", "token: $token")
                        }
                    }
                }
            } else {
                Log.e("LOGIN ERROR", "ERROR")
                showToast("Login Failed: Invalid data, Please check your input.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}