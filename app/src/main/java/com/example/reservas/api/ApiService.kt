package com.example.reservas.api

import com.example.reservas.model.Instalacion
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("instalaciones")
    fun obtenerInstalaciones(): Call<List<Instalacion>>

}