package com.example.reservas.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.reservas.R

@Composable
fun VerifyCodeScreen(
    email: String,
    onVerify: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.utez),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Verificar código",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E5A31)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Código enviado a $email",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Código") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (code == "1234") {
                    onVerify(code)
                } else {
                    error = "Código incorrecto"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verificar")
        }

        if (error.isNotEmpty()) {
            Text(error, color = Color.Red)
        }
    }
}