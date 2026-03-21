package com.example.reservas

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter

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
    userName: String = "",
    reservasExistentes: SnapshotStateList<Reserva>,
    onNavigateToReservations: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var showReservationDialog by remember { mutableStateOf(false) }
    var selectedCancha by remember { mutableStateOf<Cancha?>(null) }

    val canchas = listOf(
        Cancha(
            "Cancha de Football 7",
            listOf(R.drawable.fut1, R.drawable.fut2, R.drawable.fut3, R.drawable.fut4)
        ),
        Cancha(
            "Cancha de Basketball",
            listOf(R.drawable.basket1, R.drawable.basket2, R.drawable.basket3, R.drawable.basket4)
        ),
        Cancha(
            "Auditorio",
            listOf(R.drawable.nave1, R.drawable.nave2, R.drawable.nave3, R.drawable.nave4)
        ),
        Cancha(
            "Alberca",
            listOf(R.drawable.pool1, R.drawable.pool2, R.drawable.pool3, R.drawable.pool4),
            disponible = false
        ),
        Cancha(
            "Cancha Volleyball",
            listOf(R.drawable.volley1, R.drawable.volley2, R.drawable.volley3, R.drawable.volley4)
        )
    )

    val saludo = if (userName.isNotBlank()) "Bienvenida $userName" else "Bienvenido"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Menú",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38663A)
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = Color.Red) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            onLogout()
                        }
                    },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
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
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF4E7044))
                    }
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
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        selected = false,
                        onClick = onNavigateToReservations
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(35.dp)) },
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
                        CanchaCard(cancha) {
                            if (cancha.disponible) {
                                selectedCancha = cancha
                                showReservationDialog = true
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
                        Toast.makeText(context, "Reserva confirmada con éxito", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}

@Composable
fun ActiveReservationsScreen(
    reservasExistentes: SnapshotStateList<Reserva>,
    onNavigateToDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Filtrar reservas que no han pasado
    val now = LocalDateTime.now()
    val reservasActivas = reservasExistentes.filter { 
        LocalDateTime.of(it.fecha, it.horaFin).isAfter(now)
    }

    var reservaParaEditar by remember { mutableStateOf<Reserva?>(null) }
    var reservaParaCancelar by remember { mutableStateOf<Reserva?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Menú", modifier = Modifier.padding(16.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38663A))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = Color.Red) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close(); onLogout() } },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF4E7044))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Image(painter = painterResource(id = R.drawable.utez), contentDescription = "Logo UTEZ", modifier = Modifier.height(60.dp))
                    Spacer(modifier = Modifier.weight(1f))
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFFF0F0F0), contentColor = Color(0xFF4E7044)) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        selected = true,
                        onClick = {}
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(35.dp)) },
                        selected = false,
                        onClick = onNavigateToDashboard
                    )
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
                Text("Mis Reservas", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38663A), modifier = Modifier.padding(vertical = 16.dp))
                
                if (reservasActivas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No tienes reservas activas", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(reservasActivas) { reserva ->
                            ActiveReservaCard(
                                reserva = reserva,
                                onEdit = { reservaParaEditar = it },
                                onCancel = { reservaParaCancelar = it }
                            )
                        }
                    }
                }
            }
        }
    }

    if (reservaParaEditar != null) {
        EditTimeDialog(
            reserva = reservaParaEditar!!,
            reservasExistentes = reservasExistentes,
            onDismiss = { reservaParaEditar = null },
            onConfirm = { id, nuevaHoraInicio, nuevaHoraFin ->
                val index = reservasExistentes.indexOfFirst { it.id == id }
                if (index != -1) {
                    reservasExistentes[index] = reservasExistentes[index].copy(
                        horaInicio = nuevaHoraInicio,
                        horaFin = nuevaHoraFin
                    )
                }
                reservaParaEditar = null
                Toast.makeText(context, "Horario actualizado", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (reservaParaCancelar != null) {
        AlertDialog(
            onDismissRequest = { reservaParaCancelar = null },
            title = { Text("Confirmar cancelación") },
            text = { Text("¿Estás seguro de que deseas cancelar esta reserva?") },
            confirmButton = {
                TextButton(onClick = {
                    reservasExistentes.removeIf { it.id == reservaParaCancelar!!.id }
                    reservaParaCancelar = null
                    Toast.makeText(context, "Reserva cancelada", Toast.LENGTH_SHORT).show()
                }) { Text("Sí, cancelar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { reservaParaCancelar = null }) { Text("No") }
            }
        )
    }
}

@Composable
fun ActiveReservaCard(reserva: Reserva, onEdit: (Reserva) -> Unit, onCancel: (Reserva) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = reserva.imagenes[0]),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(reserva.canchaNombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Fecha: ${reserva.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}", color = Color.Gray)
                    Text("Hora: ${reserva.horaInicio} - ${reserva.horaFin}", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onEdit(reserva) }) { Text("Editar", color = Color(0xFF436B3B)) }
                TextButton(onClick = { onCancel(reserva) }) { Text("Cancelar reserva", color = Color.Red) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTimeDialog(
    reserva: Reserva,
    reservasExistentes: List<Reserva>,
    onDismiss: () -> Unit,
    onConfirm: (String, LocalTime, LocalTime) -> Unit
) {
    var startTime by remember { mutableStateOf(reserva.horaInicio) }
    var endTime by remember { mutableStateOf(reserva.horaFin) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Editar Horario", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showStartTimePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(startTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                    }
                    OutlinedButton(onClick = { showEndTimePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(endTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        val duration = Duration.between(startTime, endTime)
                        if (duration.isNegative || duration.isZero || duration.toMinutes() > 60) {
                            Toast.makeText(context, "Límite 1 hora y hora fin debe ser posterior", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (endTime.isAfter(LocalTime.of(22, 0))) {
                            Toast.makeText(context, "Límite 10:00 PM", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        val conflict = reservasExistentes.any { r ->
                            r.id != reserva.id && r.canchaNombre == reserva.canchaNombre && r.fecha == reserva.fecha &&
                            ((startTime.isBefore(r.horaFin) && startTime.isAfter(r.horaInicio)) ||
                             (endTime.isBefore(r.horaFin) && endTime.isAfter(r.horaInicio)) ||
                             (startTime == r.horaInicio))
                        }
                        
                        if (conflict) {
                            Toast.makeText(context, "Horario ocupado", Toast.LENGTH_SHORT).show()
                        } else {
                            onConfirm(reserva.id, startTime, endTime)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB6D))
                ) { Text("Guardar Cambios") }
            }
        }
    }

    if (showStartTimePicker) {
        val state = rememberTimePickerState(initialHour = startTime.hour, initialMinute = startTime.minute, is24Hour = true)
        CustomTimePickerDialog(onDismiss = { showStartTimePicker = false }, onConfirm = { startTime = LocalTime.of(state.hour, state.minute); showStartTimePicker = false }) {
            TimePicker(state = state)
        }
    }
    if (showEndTimePicker) {
        val state = rememberTimePickerState(initialHour = endTime.hour, initialMinute = endTime.minute, is24Hour = true)
        CustomTimePickerDialog(onDismiss = { showEndTimePicker = false }, onConfirm = { endTime = LocalTime.of(state.hour, state.minute); showEndTimePicker = false }) {
            TimePicker(state = state)
        }
    }
}

@Composable
fun CanchaCard(cancha: Cancha, onReserveClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = cancha.imagenes[0]), contentDescription = null, modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = cancha.nombre, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Button(
                    onClick = onReserveClick,
                    modifier = Modifier.height(35.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (cancha.disponible) Color(0xFF436B3B) else Color(0xFFE57373)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) { Text(text = if (cancha.disponible) "Reservar Cancha" else "Fuera de servicio", fontSize = 14.sp, color = Color.White) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDialog(
    cancha: Cancha,
    reservasExistentes: List<Reserva>,
    onDismiss: () -> Unit,
    onConfirm: (Reserva) -> Unit
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { cancha.imagenes.size })
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth(0.9f).wrapContentHeight().padding(16.dp), shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = cancha.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))) { page ->
                        Image(painter = painterResource(id = cancha.imagenes[page]), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        repeat(cancha.imagenes.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                            Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Fecha", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4E7044))
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.5.dp, Color(0xFF4E7044)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Fecha", fontSize = 16.sp)
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Horario deseado (Límite 1 hora)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4E7044))
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showStartTimePicker = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.5.dp, Color(0xFF4E7044)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)) {
                            Text(text = startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "De: HH:MM", fontSize = 14.sp)
                        }
                        OutlinedButton(onClick = { showEndTimePicker = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.5.dp, Color(0xFF4E7044)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)) {
                            Text(text = endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "A: HH:MM", fontSize = 14.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (selectedDate == null || startTime == null || endTime == null) { Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show(); return@Button }
                        val duration = Duration.between(startTime, endTime)
                        if (duration.isNegative || duration.isZero || duration.toMinutes() > 60) { Toast.makeText(context, "Límite 1 hora y hora fin debe ser posterior", Toast.LENGTH_SHORT).show(); return@Button }
                        if (endTime!!.isAfter(LocalTime.of(22, 0))) { Toast.makeText(context, "Límite 10:00 PM", Toast.LENGTH_SHORT).show(); return@Button }
                        val conflict = reservasExistentes.any { r -> r.canchaNombre == cancha.nombre && r.fecha == selectedDate && ((startTime!!.isBefore(r.horaFin) && startTime!!.isAfter(r.horaInicio)) || (endTime!!.isBefore(r.horaFin) && endTime!!.isAfter(r.horaInicio)) || (startTime!! == r.horaInicio)) }
                        if (conflict) { Toast.makeText(context, "Horario ocupado", Toast.LENGTH_LONG).show() }
                        else { onConfirm(Reserva(canchaNombre = cancha.nombre, imagenes = cancha.imagenes, fecha = selectedDate!!, horaInicio = startTime!!, horaFin = endTime!!)) }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DCB6D))
                ) { Text("Confirmar selección", fontSize = 18.sp, color = Color.White) }
            }
        }
    }
    if (showDatePicker) {
        val state = rememberDatePickerState(selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                val now = LocalDate.now(); val maxDate = now.plusMonths(1).withDayOfMonth(now.plusMonths(1).lengthOfMonth())
                return !date.isBefore(now) && !date.isAfter(maxDate) && date.year == now.year
            }
            override fun isSelectableYear(year: Int): Boolean = year == LocalDate.now().year
        })
        DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { state.selectedDateMillis?.let { selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() }; showDatePicker = false }) { Text("OK") } }) { DatePicker(state = state) }
    }
    if (showStartTimePicker) {
        val state = rememberTimePickerState(initialHour = LocalTime.now().hour, initialMinute = LocalTime.now().minute, is24Hour = true)
        CustomTimePickerDialog(onDismiss = { showStartTimePicker = false }, onConfirm = { startTime = LocalTime.of(state.hour, state.minute); showStartTimePicker = false }) { TimePicker(state = state) }
    }
    if (showEndTimePicker) {
        val state = rememberTimePickerState(initialHour = startTime?.hour ?: LocalTime.now().hour, initialMinute = startTime?.minute ?: LocalTime.now().minute, is24Hour = true)
        CustomTimePickerDialog(onDismiss = { showEndTimePicker = false }, onConfirm = { endTime = LocalTime.of(state.hour, state.minute); showEndTimePicker = false }) { TimePicker(state = state) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(onDismiss: () -> Unit, onConfirm: () -> Unit, content: @Composable () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp, modifier = Modifier.width(IntrinsicSize.Min).height(IntrinsicSize.Min)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Selecciona la hora", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 20.dp))
                content()
                Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    TextButton(onClick = onConfirm) { Text("Aceptar") }
                }
            }
        }
    }
}
