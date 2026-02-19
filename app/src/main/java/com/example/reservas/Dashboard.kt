package com.example.reservas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Cancha(
    val nombre: String,
    val imagenRes: Int,
    val disponible: Boolean = true
)

@Composable
fun DashboardScreen(userName: String = "") {
    val canchas = listOf(
        Cancha("Cancha de football 7", R.drawable.utez),
        Cancha("Cancha de Basquetbol", R.drawable.utez),
        Cancha("Auditorio", R.drawable.utez),
        Cancha("Piscina", R.drawable.utez, disponible = false),
        Cancha("Cancha volleyball", R.drawable.utez)
    )

    // Lógica para el saludo
    val saludo = if (userName.isNotBlank()) "Bienvenida $userName" else "Bienvenido"

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF4E7044))
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.utez),
                    contentDescription = "Logo UTEZ",
                    modifier = Modifier.height(60.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF0F0F0),
                contentColor = Color(0xFF4E7044)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Place, contentDescription = null) },
                    selected = false,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(35.dp)) },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    selected = false,
                    onClick = {}
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = saludo,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38663A)
            )
            
            Text(
                text = "Elige tu próxima reserva",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(canchas) { cancha ->
                    CanchaCard(cancha)
                }
            }
        }
    }
}

@Composable
fun CanchaCard(cancha: Cancha) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = cancha.imagenRes),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cancha.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Button(
                    onClick = { /* Acción */ },
                    modifier = Modifier.height(35.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (cancha.disponible) Color(0xFF436B3B) else Color(0xFFE57373)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = if (cancha.disponible) "Ver disponibilidad" else "Fuera de servicio",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
