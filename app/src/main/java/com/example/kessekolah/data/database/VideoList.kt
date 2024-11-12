package com.example.kessekolah.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoList(
    val videoId: String = "",
    val uid: String = "",
    val judul: String = "",
    val category: String = "",
    val videoUrl: String = "",
    val timestamp: String = "",
    val dataIlus: Int = 0,
    val backColorBanner: String = ""
) : Parcelable
