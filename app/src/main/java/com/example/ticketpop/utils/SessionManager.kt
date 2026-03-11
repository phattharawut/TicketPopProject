package com.example.ticketpop.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.ticketpop.data.model.UserProfile

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    fun saveUser(user: UserProfile, token: String?) {
        prefs.edit()
            .putString(Constants.KEY_JWT_TOKEN, token)
            .putString(Constants.KEY_USER_ID, user.id)
            .putString(Constants.KEY_USER_ROLE, user.role)
            .putString("user_fullName", user.fullName)
            .putString("user_email", user.email)
            .putString("user_phone", user.phone)
            .putString("user_level", user.level)
            .apply()
    }

    fun getUser(): UserProfile? {
        val id = prefs.getString(Constants.KEY_USER_ID, null) ?: return null
        return UserProfile(
            id = id,
            fullName = prefs.getString("user_fullName", "") ?: "",
            email = prefs.getString("user_email", "") ?: "",
            phone = prefs.getString("user_phone", "") ?: "",
            role = prefs.getString(Constants.KEY_USER_ROLE, "Customer") ?: "Customer",
            level = prefs.getString("user_level", "Bronze") ?: "Bronze"
        )
    }

    fun getToken(): String? = prefs.getString(Constants.KEY_JWT_TOKEN, null)

    fun isLoggedIn(): Boolean = prefs.contains(Constants.KEY_USER_ID)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
