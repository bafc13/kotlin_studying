package com.example.lab1_bafc13.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1_bafc13.PrefsManager
import com.example.lab1_bafc13.models.FavoriteRadar
import com.example.lab1_bafc13.models.Radar
import com.example.lab1_bafc13.models.User
import com.example.lab1_bafc13.rest.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.IllegalStateException
import kotlin.String

class MapViewModel () : ViewModel() {

    private lateinit var context: Context
    private var _prefsManager: PrefsManager? = null
    private val prefsManager
        get() = _prefsManager ?: throw IllegalStateException("Prefs Manager in Map VM is null")

    private val _radars = MutableLiveData<List<Radar>>()
    val radars: LiveData<List<Radar>> = _radars

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

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

    fun getState(): String? {
        _prefsManager = PrefsManager(context)
        return prefsManager.getState()
    }

    fun loadRadars(){
        viewModelScope.launch {
            try {
                if(getState() == "Калужская область") {
                    _isLoading.value = true
                    val response = RetrofitClient.apiService.getKalugaRadars()
                    _radars.value = response
                    _error.value = null
                } else {
                    _isLoading.value = true
                    val response = RetrofitClient.apiService.getMoscowRadars()
                    _radars.value = response
                    _error.value = null
                }
            } catch (e: HttpException) {
                Log.e("RadarApi", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveToFavorites(radar: Radar) {
        val userUuid = getUuid()

        val favoriteRadar = FavoriteRadar(
            userUuid!!,
            radar.camera_id,
            radar.region_code,
            radar.src_camera,
            radar.serial_no,
            radar.print_name,
            radar.camera_model,
            radar.complex_id,
            radar.camera_place,
            radar.gps_x,
            radar.gps_y,
            radar.violname
        )

        viewModelScope.launch {
            RetrofitClient.apiService.saveFavoriteRadar(favoriteRadar)
        }
    }

}