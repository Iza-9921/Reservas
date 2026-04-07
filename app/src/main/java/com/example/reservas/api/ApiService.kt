package com.example.reservas.api

import com.example.reservas.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("instalaciones")
    fun obtenerInstalaciones(): Call<List<Instalacion>>

    // Prueba con estas rutas comunes
    @POST("usuarios")
    fun registro(@Body usuario: Usuario): Call<LoginResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("reservas")
    fun crearReserva(@Body reserva: ReservaRequest): Call<ReservaResponse>

}