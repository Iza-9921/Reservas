package com.example.reservas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.reservas.screens.ForgotPasswordScreen
import com.example.reservas.screens.LoginScreen
import com.example.reservas.screens.RegisterScreen
import com.example.reservas.ui.theme.ReservasTheme

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object ForgotPassword : Screen()
    object Dashboard : Screen()
    object ActiveReservations : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReservasTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                var userName by remember { mutableStateOf("") }
                var userId by remember { mutableStateOf(0) }
                
                val reservasExistentes = remember { mutableStateListOf<Reserva>() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            is Screen.Login -> LoginScreen(
                                onNavigateToRegister = { currentScreen = Screen.Register },
                                onNavigateToForgotPassword = { currentScreen = Screen.ForgotPassword },
                                onNavigateToDashboard = { name -> 
                                    userName = name
                                    // Aquí podrías guardar el ID real que venga de la API
                                    currentScreen = Screen.Dashboard 
                                }
                            )
                            is Screen.Register -> RegisterScreen(
                                onNavigateBack = { currentScreen = Screen.Login },
                                onRegisterSuccess = { name ->
                                    currentScreen = Screen.Login
                                }
                            )
                            is Screen.ForgotPassword -> ForgotPasswordScreen(
                                onNavigateBack = { currentScreen = Screen.Login }
                            )
                            is Screen.Dashboard -> DashboardScreen(
                                userName = userName,
                                userId = userId,
                                reservasExistentes = reservasExistentes,
                                onNavigateToReservations = { currentScreen = Screen.ActiveReservations },
                                onLogout = { 
                                    reservasExistentes.clear()
                                    currentScreen = Screen.Login 
                                }
                            )
                            is Screen.ActiveReservations -> ActiveReservationsScreen(
                                reservasExistentes = reservasExistentes,
                                onNavigateToDashboard = { currentScreen = Screen.Dashboard },
                                onLogout = {
                                    reservasExistentes.clear()
                                    currentScreen = Screen.Login
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
