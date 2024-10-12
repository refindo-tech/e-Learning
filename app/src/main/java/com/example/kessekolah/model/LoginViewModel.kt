package com.example.kessekolah.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.di.auth.AuthUser
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.data.repo.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val repository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    fun userLogin(email: String, password: String) = repository.loginUser(email, password)

    fun saveUser(user: LoginData) {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }
}