package com.example.lab1_bafc13.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1_bafc13.PrefsManager
import com.example.lab1_bafc13.models.FavoriteRadar
import com.example.lab1_bafc13.models.Radar
import com.example.lab1_bafc13.rest.RetrofitClient
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class FavoriteRadarViewModel : ViewModel() {

    private val _radars = MutableLiveData<List<FavoriteRadar>>()
    val radars: LiveData<List<FavoriteRadar>> = _radars

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private lateinit var context: Context
    private var _prefsManager: PrefsManager? = null
    private val prefsManager
        get() = _prefsManager ?: throw IllegalStateException("Prefs Manager in Map VM is null")

    fun initContext(context: Context) {
        this.context = context
    }

    fun loadRadars() {
        viewModelScope.launch {
            try {
                _prefsManager = PrefsManager(context)
                _isLoading.value = true
                val response = RetrofitClient.apiService.getFavoriteRadarsByUuid("eq." + prefsManager.getUuid())
                _radars.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки избранных радаров: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteRadar(radar: FavoriteRadar) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteFavoriteRadar("eq." + radar.user_uuid, "eq." + radar.gps_y, "eq." + radar.gps_x)
                loadRadars()
            } catch (e: Exception) {
                _error.value = "Ошибка удаления радара: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}