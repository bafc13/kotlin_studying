package com.example.lab1_bafc13.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.lab1_bafc13.PrefsManager
import java.lang.IllegalStateException

class UserViewModel () : ViewModel() {

    private lateinit var context: Context
    private var _prefsManager: PrefsManager? = null
    private val prefsManager
        get() = _prefsManager ?: throw IllegalStateException("Prefs Manager in User VM is null")

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

    fun getState(): String? {
        _prefsManager = PrefsManager(context)
        return prefsManager.getState()
    }

    fun setState(state: String) {
        _prefsManager = PrefsManager(context)
        prefsManager.saveState(state)
    }
}