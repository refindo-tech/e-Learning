package com.example.kessekolah.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ButtonCoreFeatures(
    val icon: String,
    val title: String,
) : Parcelable