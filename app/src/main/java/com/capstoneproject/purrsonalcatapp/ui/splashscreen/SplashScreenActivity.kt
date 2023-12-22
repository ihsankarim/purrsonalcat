package com.capstoneproject.purrsonalcatapp.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.capstoneproject.purrsonalcatapp.R
import com.capstoneproject.purrsonalcatapp.ui.main.MainActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var imgSplash: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        imgSplash = findViewById(R.id.img_splash)

        imgSplash.alpha = 0f
        imgSplash.animate().setDuration(1500).alpha(1f).start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, splashTimeOut)

    }

    companion object {
        private val splashTimeOut: Long = 2000
    }
}
