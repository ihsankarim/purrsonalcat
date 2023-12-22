package com.capstoneproject.purrsonalcatapp.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.capstoneproject.purrsonalcatapp.R
import com.capstoneproject.purrsonalcatapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ARTIKEL = "extra_artikel"
    }

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataArtikel = intent.getParcelableExtra<Artikel>(EXTRA_ARTIKEL)

        val tvDetailName: TextView = findViewById(R.id.text_judul)
        val tvDetailDescription: TextView = findViewById(R.id.text_des)
        val ivDetailPhoto: ImageView = findViewById(R.id.img_photo)

        if (dataArtikel != null) {
            tvDetailName.text = dataArtikel.judul
            tvDetailDescription.text = dataArtikel.description
            ivDetailPhoto.setImageResource(dataArtikel.photo)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}