package com.example.reservas.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.reservas.model.LoginRequest
import com.example.reservas.model.LoginResponse
import com.example.reservas.model.Usuario
import com.example.reservas.network.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _loginResponse = mutableStateOf<LoginResponse?>(null)
    val loginResponse: State<LoginResponse?> = _loginResponse

    private val _registerResponse = mutableStateOf<LoginResponse?>(null)
    val registerResponse: State<LoginResponse?> = _registerResponse

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun login(correo: String, contrasena: String, onSuccess: (Usuario) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        val request = LoginRequest(correo, contrasena)
        RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body()?.success == true) {
                    _loginResponse.value = response.body()
                    response.body()?.usuario?.let { onSuccess(it) }
                } else {
                    _errorMessage.value = parseError(response)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error de conexión: ${t.message}"
            }
        })
    }

    fun register(nombre: String, correo: String, contrasena: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        val usuario = Usuario(nombre_completo = nombre, correo = correo, contrasena = contrasena)
        RetrofitClient.instance.registro(usuario).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body()?.success == true) {
                    _registerResponse.value = response.body()
                    onSuccess()
                } else {
                    _errorMessage.value = parseError(response)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error de conexión: ${t.message}"
            }
        })
    }

    private fun parseError(response: Response<*>): String {
        val code = response.code()
        return try {
            val errorBody = response.errorBody()?.string()
            val jsonObject = JSONObject(errorBody)
            jsonObject.optString("message", "Error del servidor ($code)")
        } catch (e: Exception) {
            "Error HTTP $code: ${response.message()}"
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
