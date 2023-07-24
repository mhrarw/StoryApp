package com.example.storyapp

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    fun saveSessionData(userId: String, token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("data_sesi", userId)
        editor.putString("token", token)
        editor.apply()
    }

    fun getSessionUserId(): String? {
        return sharedPreferences.getString("data_sesi", null)
    }

    fun getSessionToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun clearSessionData() {
        sharedPreferences.edit().clear().apply()
    }

}