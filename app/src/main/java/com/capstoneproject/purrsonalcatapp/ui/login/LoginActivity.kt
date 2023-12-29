package com.capstoneproject.purrsonalcatapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
        viewModel.isLoading.observe(this@LoginActivity, { isLoading ->
            showLoading(isLoading)
        })

        binding.btnRegister.setOnClickListener {
            viewModel.setLoading(false)
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }



        viewModel.loginResult.observe(this) { response ->
            if (!response.error!!) {
                viewModel.setLoading(false)
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
                viewModel.setLoading(false)
                Log.e("LOGIN ERROR", "ERROR")
                showToast("Login Failed: Invalid data, Please check your input.")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                imageView2.visibility = View.GONE
                underlineLogo.visibility = View.GONE
                tvDontHaveAccount.visibility = View.GONE
                titleTextView.visibility = View.GONE
                emailEditTextView.visibility = View.GONE
                passwordEditTextLayout.visibility = View.GONE
                passwordTextView.visibility = View.GONE
                passwordEditText.visibility = View.GONE
                emailEditText.visibility = View.GONE
                emailTextView.visibility = View.GONE
                messageTextView.visibility = View.GONE
                btnRegister.visibility = View.GONE
                loginButton.visibility = View.GONE
            }
        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                imageView2.visibility = View.VISIBLE
                underlineLogo.visibility = View.VISIBLE
                tvDontHaveAccount.visibility = View.VISIBLE
                titleTextView.visibility = View.VISIBLE
                emailEditTextView.visibility = View.VISIBLE
                passwordEditTextLayout.visibility = View.VISIBLE
                passwordTextView.visibility = View.VISIBLE
                passwordEditText.visibility = View.VISIBLE
                emailEditText.visibility = View.VISIBLE
                emailTextView.visibility = View.VISIBLE
                messageTextView.visibility = View.VISIBLE
                btnRegister.visibility = View.VISIBLE
                loginButton.visibility = View.VISIBLE
            }
        }
    }
}