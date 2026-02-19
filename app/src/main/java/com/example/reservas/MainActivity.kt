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

// Definición de pantallas para navegación simple
sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object ForgotPassword : Screen()
    object Dashboard : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReservasTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                var userName by remember { mutableStateOf("") }

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
                                onNavigateBack = { currentScreen = Screen.Login }
                            )
                            is Screen.Dashboard -> DashboardScreen(
                                userName = userName
                            )
                        }
                    }
                }
            }
        }
    }
}
