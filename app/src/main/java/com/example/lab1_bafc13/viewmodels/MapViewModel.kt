package com.example.lab1_bafc13.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.lab1_bafc13.PrefsManager
import com.example.lab1_bafc13.models.User
import com.example.lab1_bafc13.rest.RetrofitClient
import java.lang.IllegalStateException

class MapViewModel () : ViewModel() {

    private lateinit var context: Context
    private var _prefsManager: PrefsManager? = null
    private val prefsManager
        get() = _prefsManager ?: throw IllegalStateException("Prefs Manager in Map VM is null")

    fun initContext(context: Context) {
        this.context = context
    }

    fun getUuid(): String? {
        _prefsManager = PrefsManager(context)
        return prefsManager.getUuid()
    }

    fun getUserName(): String? {
        _prefsManager = PrefsManager(context)
        return prefsManager.getUserName()
    }

    suspend fun getUserByUuid(uuid: String): List<User> {
        _prefsManager = PrefsManager(context)
        return RetrofitClient.apiService.getUserByUuid(uuid)
    }

    suspend fun createNewUser(newUuid: String, newUser: User) {
        _prefsManager = PrefsManager(context)
        RetrofitClient.apiService.createUser(newUser)
        prefsManager.saveUuid(newUuid)
        prefsManager.saveUserName(newUser.name)
        prefsManager.setFirstLaunchComplete()
    }
}