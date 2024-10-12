package com.example.kessekolah.data.repo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.database.UserDao
import com.example.kessekolah.data.database.UserRoomDatabase
import com.example.kessekolah.data.di.auth.AuthUser
import com.example.kessekolah.data.remote.LoginData
import com.example.kessekolah.data.response.ResponseMessage
import com.example.kessekolah.utils.LoginPreference
import com.google.android.recaptcha.RecaptchaException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AuthRepository(
    application: Application,
    private val loginPreference: LoginPreference,
    private val firebaseAuth: FirebaseAuth
) {
    private val mUserDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserRoomDatabase.getDatabase(application)
        mUserDao = db.userDao()
    }

    fun signUpUser(email: String, password: String): LiveData<ResponseMessage<AuthResult>> =
        liveData { emit(ResponseMessage.Loading())
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                emit(ResponseMessage.Success(result))
            } catch(e: FirebaseAuthInvalidCredentialsException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "The email address is badly formatted"
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "The email address is already in use by another account"
                    "ERROR_WRONG_PASSWORD" -> "The password is invalid"
                    "ERROR_USER_DISABLED" -> "The user account has been disabled"
                    else -> "Invalid credentials"
                }
                emit(ResponseMessage.Error(errorMessage))
            } catch(e: RecaptchaException) {
                emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
            } catch(e: Exception) {
                emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
            }
        }


    suspend fun checkEmailExists(email: String): Boolean {
        return try {
            val signInMethods = firebaseAuth.fetchSignInMethodsForEmail(email).await()
            signInMethods.signInMethods?.isNotEmpty() == true
        } catch (e: Exception) {
            false
        }
    }

    fun loginUser(email: String, password: String): LiveData<ResponseMessage<AuthResult>> =
        liveData { emit(ResponseMessage.Loading())
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                emit(ResponseMessage.Success(result))
            } catch(e: FirebaseAuthInvalidCredentialsException) {
                val errorMessage = when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "The email address is badly formatted"
                    "ERROR_USER_NOT_FOUND" -> "There is no user record corresponding to this identifier. The user may have been deleted"
                    "ERROR_WRONG_PASSWORD" -> "The password is invalid or the user does not have a password"
                    else -> "Invalid credentials"
                }
                emit(ResponseMessage.Error(errorMessage))
            } catch(e: RecaptchaException) {
                emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
            } catch(e: Exception) {
                emit(ResponseMessage.Error(e.localizedMessage ?: "An error occurred"))
            }
        }

    fun saveUser(user: LoginData) {
        loginPreference.saveData(user)
    }


    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            application: Application,
            loginPreference: LoginPreference,
            firebaseAuth: FirebaseAuth
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(application, loginPreference, firebaseAuth)
        }.also {
            instance = it
        }
    }
}