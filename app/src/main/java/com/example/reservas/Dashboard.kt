package com.example.reservas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reservas.model.Instalacion
import com.example.reservas.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.*

// Modelos locales para la UI
data class Cancha(
    val nombre: String,
    val imagenes: List<Int>,
    val disponible: Boolean = true
)

data class Reserva(
    val id: String = java.util.UUID.randomUUID().toString(),
    val canchaNombre: String,
    val imagenes: List<Int>,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime
)

@Composable
fun DashboardScreen(
    userName: String = "Usuario",
    reservasExistentes: SnapshotStateList<Reserva>,
    onNavigateToReservations: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // --- ESTADOS PARA LA API ---
    var instalaciones by remember { mutableStateOf<List<Instalacion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    var showReservationDialog by remember { mutableStateOf(false) }
    var selectedCancha by remember { mutableStateOf<Cancha?>(null) }

    // --- LLAMADA A LA API ---
    LaunchedEffect(Unit) {
        RetrofitClient.instance.obtenerInstalaciones().enqueue(object : Callback<List<Instalacion>> {
            override fun onResponse(call: Call<List<Instalacion>>, response: Response<List<Instalacion>>) {
                if (response.isSuccessful) {
                    instalaciones = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Error del servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Instalacion>>, t: Throwable) {
                Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        })
    }

    // --- MAPEO DE DATOS API -> UI ---
    val canchas = instalaciones.map { inst ->
        Cancha(
            nombre = inst.nombre,
            imagenes = obtenerImagenesPorTipo(inst.nombre),
            disponible = inst.estado.equals("disponible", ignoreCase = true)
        )
    }

    val saludo = "Bienvenido, $userName"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = saludo,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Mis reservas") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            onNavigateToReservations()
                        }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = Color.Red) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            onLogout()
                        }
                    },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text("Reservas", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.weight(1f))
                }
            },

            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        selected = false,
                        onClick = onNavigateToReservations
                    )

                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        selected = true,
                        onClick = {}
                    )
                }
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = saludo,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Selecciona una cancha")

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF38663A))
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(canchas) { cancha ->
                            CanchaCard(cancha) {
                                if (cancha.disponible) {
                                    selectedCancha = cancha
                                    showReservationDialog = true
                                } else {
                                    Toast.makeText(context, "No disponible", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }

            if (showReservationDialog && selectedCancha != null) {
                ReservationDialog(
                    cancha = selectedCancha!!,
                    reservasExistentes = reservasExistentes,
                    onDismiss = { showReservationDialog = false },
                    onConfirm = { nuevaReserva ->
                        reservasExistentes.add(nuevaReserva)
                        showReservationDialog = false

                        Toast.makeText(context, "Reserva creada", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}

// Función auxiliar para asignar imágenes locales según el nombre de la API
fun obtenerImagenesPorTipo(nombre: String): List<Int> {
    return when {
        nombre.contains("Football", true) || nombre.contains("Futbol", true) -> 
            listOf(R.drawable.fut1, R.drawable.fut2)
        nombre.contains("Basketball", true) || nombre.contains("Basquetbol", true) -> 
            listOf(R.drawable.basket1, R.drawable.basket2)
        nombre.contains("Auditorio", true) -> 
            listOf(R.drawable.nave1, R.drawable.nave2)
        nombre.contains("Alberca", true) -> 
            listOf(R.drawable.pool1)
        else -> 
            listOf(R.drawable.volley1, R.drawable.volley2)
    }
}

@Composable
fun CanchaCard(
    cancha: Cancha,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = cancha.nombre,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = if (cancha.disponible) "Disponible" else "No disponible",
                color = if (cancha.disponible) Color(0xFF2E7D32) else Color.Red
            )
        }
    }
}

@Composable
fun ReservationDialog(
    cancha: Cancha,
    reservasExistentes: List<Reserva>,
    onDismiss: () -> Unit,
    onConfirm: (Reserva) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val nueva = Reserva(
                    canchaNombre = cancha.nombre,
                    imagenes = cancha.imagenes,
                    fecha = LocalDate.now(),
                    horaInicio = LocalTime.now(),
                    horaFin = LocalTime.now().plusHours(1)
                )
                onConfirm(nueva)
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Reservar") },
        text = { Text("Reservando ${cancha.nombre}") }
    )
}

@Composable
fun ActiveReservationsScreen(
    reservasExistentes: SnapshotStateList<Reserva>,
    onNavigateToDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Reservas activas")

        reservasExistentes.forEach {
            Text(it.canchaNombre)
        }

        Button(onClick = onNavigateToDashboard) {
            Text("Volver")
        }
    }
}
