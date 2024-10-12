package com.example.kessekolah.data.di.auth

data class AuthUser(
    val name: String,
    val token: String,
    val isLogin: Boolean,
)