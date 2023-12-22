package com.capstoneproject.purrsonalcatapp.ui.detail

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artikel(
    val judul: String,
    val description: String,
    val photo: Int
):Parcelable