package com.example.kessekolah.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginData(
    val token: String?,
    var name: String?,
    val email: String?,
    val role: String?,
    var profilePicture: String?,
    val isLogin: Boolean
):Parcelable
