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
import com.example.reservas.screens.VerifyCodeScreen
import com.example.reservas.screens.NewPasswordScreen
import com.example.reservas.screens.SuccessScreen


sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object ForgotPassword : Screen()
    object Dashboard : Screen()
    object ActiveReservations : Screen()

    data class VerifyCode(val email: String) : Screen()

    object NewPassword : Screen()

    object Success : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReservasTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                var userName by remember { mutableStateOf("") }

                // Estado compartido de reservas
                val reservasExistentes = remember { mutableStateListOf<Reserva>() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            is Screen.Login -> LoginScreen(
                                onNavigateToRegister = { currentScreen = Screen.Register },
                                onNavigateToForgotPassword = { currentScreen = Screen.ForgotPassword },
                                onNavigateToDashboard = { currentScreen = Screen.Dashboard }
                            )
                            is Screen.Register -> RegisterScreen(
                                onNavigateBack = { currentScreen = Screen.Login },
                                onRegisterSuccess = { name ->
                                    userName = name
                                    currentScreen = Screen.Login
                                }
                            )
                            is Screen.ForgotPassword -> ForgotPasswordScreen(
                                onSendCode = { email ->
                                    currentScreen = Screen.VerifyCode(email)
                                },
                                onNavigateBack = { currentScreen = Screen.Login }
                            )
                            is Screen.Dashboard -> DashboardScreen(
                                userName = userName,
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
                            is Screen.VerifyCode -> VerifyCodeScreen(
                                email = (currentScreen as Screen.VerifyCode).email,
                                onVerify = {
                                    currentScreen = Screen.NewPassword
                                }
                            )
                            is Screen.NewPassword -> NewPasswordScreen(
                                onSuccess = {
                                    currentScreen = Screen.Success
                                }
                            )
                            is Screen.Success -> SuccessScreen(
                                onStart = { currentScreen = Screen.Login }
                            )
                        }
                    }
                }
            }
        }
    }
}
