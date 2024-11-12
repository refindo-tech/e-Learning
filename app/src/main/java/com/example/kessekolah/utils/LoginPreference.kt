package com.example.kessekolah.utils

import android.content.Context
import com.example.kessekolah.data.database.User
import com.example.kessekolah.data.remote.LoginData

class LoginPreference(context: Context) {

    private val preference = context.getSharedPreferences("login", Context.MODE_PRIVATE)

    fun saveData(data: LoginData) {
        val editor = preference.edit()
        editor.putString("token", data.token)
        editor.putString("name", data.name)
        editor.putString("email", data.email)
        editor.putString("role", data.role)
        editor.putString("profilePicture", data.profilePicture)
        editor.putBoolean("isLogin", true)
        editor.apply()
    }

    fun getData(): LoginData {
        val token = preference.getString("token", "")
        val name = preference.getString("name", "")
        val email = preference.getString("email", "")
        val role = preference.getString("role", "")
        val profilePicture = preference.getString("profilePicture", "")
        val isLogin = preference.getBoolean("isLogin", false)

        return LoginData(token, name, email, role, profilePicture, isLogin)
    }

    fun removeData() {
        var editor = preference.edit()
        editor.remove("token")
        editor.remove("username")
        editor.remove("nama")
        editor.remove("email")
        editor.remove("role")
        editor.remove("profilePicture")
        editor.remove("isLogin")
        editor.apply()
    }

    fun getRememberMeStatus(): Boolean {
        return preference.getBoolean("rememberMe", false)
    }

    fun setRememberMeStatus(status: Boolean) {
        val editor = preference.edit()
        editor.putBoolean("rememberMe", status)
        editor.apply()
    }

    fun setUserEmail(email: String) {
        val editor = preference.edit()
        editor.putString("emailRememberMe", email)
        editor.apply()
    }

    fun getUserEmail(): String {
        return preference.getString("emailRememberMe", "") ?: ""
    }
}