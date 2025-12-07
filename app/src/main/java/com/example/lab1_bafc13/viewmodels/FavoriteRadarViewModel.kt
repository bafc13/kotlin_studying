package com.example.lab1_bafc13.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab1_bafc13.models.FavoriteRadar
import com.example.lab1_bafc13.rest.RetrofitClient
import kotlinx.coroutines.launch

class FavoriteRadarViewModel : ViewModel() {

    private val _radars = MutableLiveData<List<FavoriteRadar>>()
    val radars: LiveData<List<FavoriteRadar>> = _radars

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadRadars() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitClient.apiService.getRadars()
                _radars.value = response
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки избранных радаров: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}