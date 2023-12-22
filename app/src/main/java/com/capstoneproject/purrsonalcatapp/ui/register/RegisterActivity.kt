package com.capstoneproject.purrsonalcatapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.purrsonalcatapp.R
import com.capstoneproject.purrsonalcatapp.data.local.pref.AuthPreferences
import com.capstoneproject.purrsonalcatapp.data.local.pref.dataStore
import com.capstoneproject.purrsonalcatapp.data.remote.retrofit.ApiConfig
import com.capstoneproject.purrsonalcatapp.data.repository.AuthRepository
import com.capstoneproject.purrsonalcatapp.databinding.ActivityMainBinding
import com.capstoneproject.purrsonalcatapp.databinding.ActivityRegisterBinding
import com.capstoneproject.purrsonalcatapp.ui.AuthViewModelFactory
import com.capstoneproject.purrsonalcatapp.ui.login.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import android.widget.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.math.log

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var viewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authPreferences = AuthPreferences.getInstance(this.dataStore)
        setupRegister()
    }

    private fun setupRegister() {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val token = authPreferences.authToken.first()
            val apiService = ApiConfig.getApiService(token)
            val repository = AuthRepository(apiService, authPreferences)
            viewModel =
                ViewModelProvider(this@RegisterActivity, AuthViewModelFactory(repository)).get(
                    RegisterViewModel::class.java
                )

            binding.btnRegister.setOnClickListener {
                val username = binding.edRegisterName.text.toString()
                val email = binding.edRegisterEmail.text.toString()
                val password = binding.edRegisterPassword.text.toString()

                if (username.isNotEmpty() && isValidEmail(email) && isValidPassword(password)) {
                    try {
                        viewModel.registerUser(username, email, password)
                    } catch (e: Exception) {
                        showToast("Error during registration: ${e.message}")
                        Log.e("Error during registration:", "${e.message}")
                    }
                } else {
                    showToast("Register Failed: Invalid data, Please check your input.")
                }
            }

            viewModel.registerResult.observe(this@RegisterActivity) { response ->
                Log.d("OBSERVE", "Observing register result: $response")
                if (!response.error!!) {
                    binding.edRegisterName.text = null
                    binding.edRegisterEmail.text = null
                    binding.edRegisterPassword.text = null
                    showToast("Register success")
                    Log.d("REGISTER SUCCESS", "RESULT: $response")
                } else {
                    showToast("Register Failed, you must ensure the data is valid")
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    private fun showToast(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
    }
}