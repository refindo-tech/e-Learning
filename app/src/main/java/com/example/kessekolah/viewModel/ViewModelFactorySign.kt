package com.example.kessekolah.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kessekolah.data.repo.AuthRepository
import com.example.kessekolah.model.LoginViewModel
import com.example.kessekolah.model.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth

class ViewModelFactorySign private constructor(
    private val mApplication: Application,
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactorySign? = null

        @JvmStatic
        fun getInstance(application: Application, authRepository: AuthRepository, firebaseAuth: FirebaseAuth): ViewModelFactorySign {
            if (INSTANCE == null) {
                synchronized(ViewModelFactorySign::class.java) {
                    INSTANCE = ViewModelFactorySign(application, authRepository, firebaseAuth)
                }
            }
            return INSTANCE as ViewModelFactorySign
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(mApplication, authRepository, firebaseAuth) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(mApplication, authRepository, firebaseAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}