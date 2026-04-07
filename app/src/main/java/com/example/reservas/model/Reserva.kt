package com.example.reservas.model

data class ReservaRequest(
    val id_usuario: Int,
    val id_instalacion: Int,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String
)

data class ReservaResponse(
    val success: Boolean,
    val message: String
)
