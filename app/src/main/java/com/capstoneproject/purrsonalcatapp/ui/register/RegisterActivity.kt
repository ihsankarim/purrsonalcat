package com.capstoneproject.purrsonalcatapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

            viewModel.isLoading.observe(this@RegisterActivity, { isLoading ->
                showLoading(isLoading)
            })

            viewModel.registerResult.observe(this@RegisterActivity) { response ->
                Log.d("OBSERVE", "Observing register result: $response")
                if (!response.error!!) {
                    viewModel.setLoading(false)
                    binding.edRegisterName.text = null
                    binding.edRegisterEmail.text = null
                    binding.edRegisterPassword.text = null
                    showToast("Register success")
                    Log.d("REGISTER SUCCESS", "RESULT: $response")
                } else {
                    viewModel.setLoading(false)
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                underlineLogo.visibility = View.GONE
                messageTextView.visibility = View.GONE
                tvHaveAccount.visibility = View.GONE
                edRegisterEmail.visibility = View.GONE
                edRegisterName.visibility = View.GONE
                edRegisterPassword.visibility = View.GONE
                btnLogin.visibility = View.GONE
                btnRegister.visibility = View.GONE
                emailEditTextLayout.visibility = View.GONE
                nameEditTextLayout.visibility = View.GONE
                passwordEditTextLayout.visibility = View.GONE
                titleTextView.visibility = View.GONE
                emailTextView.visibility = View.GONE
                passwordTextView.visibility = View.GONE
                nameTextView.visibility = View.GONE
                btnRegister.visibility = View.GONE
                imageView.visibility = View.GONE
            }
        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                edRegisterEmail.visibility = View.VISIBLE
                messageTextView.visibility = View.VISIBLE
                edRegisterName.visibility = View.VISIBLE
                underlineLogo.visibility = View.VISIBLE
                tvHaveAccount.visibility = View.VISIBLE
                edRegisterPassword.visibility = View.VISIBLE
                btnLogin.visibility = View.VISIBLE
                btnRegister.visibility = View.VISIBLE
                emailEditTextLayout.visibility = View.VISIBLE
                nameEditTextLayout.visibility = View.VISIBLE
                passwordEditTextLayout.visibility = View.VISIBLE
                titleTextView.visibility = View.VISIBLE
                emailTextView.visibility = View.VISIBLE
                passwordTextView.visibility = View.VISIBLE
                nameTextView.visibility = View.VISIBLE
                btnRegister.visibility = View.VISIBLE
                imageView.visibility = View.VISIBLE
            }
        }
    }
}