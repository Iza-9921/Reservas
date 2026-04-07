package com.example.reservas.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.reservas.api.ApiService
import com.example.reservas.model.Instalacion
import com.example.reservas.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {
    private val _instalaciones = mutableStateOf<List<Instalacion>>(emptyList())
    val instalaciones: State<List<Instalacion>> = _instalaciones

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun fetchInstalaciones() {
        _isLoading.value = true
        RetrofitClient.instance.obtenerInstalaciones().enqueue(object : Callback<List<Instalacion>> {
            override fun onResponse(call: Call<List<Instalacion>>, response: Response<List<Instalacion>>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _instalaciones.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al obtener instalaciones: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<Instalacion>>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error de red: ${t.message}"
            }
        })
    }
}
