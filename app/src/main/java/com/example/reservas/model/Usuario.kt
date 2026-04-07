package com.example.reservas.model

data class Usuario(
    val id_usuario: Int? = null,
    val nombre_completo: String,
    val correo: String,
    val contrasena: String? = null
)

data class LoginRequest(
    val correo: String,
    val contrasena: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val usuario: Usuario?
)
