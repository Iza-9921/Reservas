package com.example.reservas.model

data class Instalacion(
    val id_instalacion: Int,
    val nombre: String,
    val tipo: String,
    val descripcion: String,
    val estado: String
)