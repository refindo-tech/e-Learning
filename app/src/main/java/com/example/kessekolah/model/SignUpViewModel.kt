package com.example.kessekolah.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kessekolah.data.repo.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignUpViewModel(
    application: Application,
    private val repository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {


    fun insertUser(email: String, password: String) = repository.signUpUser(email, password)

    fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exists = repository.checkEmailExists(email)
            callback(exists)
        }
    }
}
