package com.example.lab1_bafc13

import android.content.Context

class PrefsManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getUuid(): String? = sharedPref.getString("USER_UUID", null)

    fun saveUuid(uuid: String) {
        sharedPref.edit().putString("USER_UUID", uuid).apply()
    }

    fun getState(): String? = sharedPref.getString("USER_STATE", null)

    fun saveState(state: String) {
        sharedPref.edit().putString("USER_STATE", state).apply()
    }

    fun getUserName(): String? = sharedPref.getString("USER_NAME", null)

    fun saveUserName(name: String) {
        sharedPref.edit().putString("USER_NAME", name).apply()
    }

    fun isFirstLaunch(): Boolean = sharedPref.getBoolean("FIRST_LAUNCH", true)

    fun setFirstLaunchComplete() {
        sharedPref.edit().putBoolean("FIRST_LAUNCH", false).apply()
    }
}