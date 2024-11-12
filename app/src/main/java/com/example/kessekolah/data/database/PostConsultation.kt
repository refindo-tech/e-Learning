package com.example.kessekolah.data.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostConsultation(
    val guestId : String = "",
    val name: String = "",
    val question : String = "",
    val description: String = "",
    val imgURL: String = "",
    val timestamp: String = "",
) : Parcelable

@Parcelize
data class Answer(
    val answerId: String = "",
    val name: String = "",
    val answerText: String = "",
    val answerDesc: String = "",
    val timestamp: String = ""
) : Parcelable
