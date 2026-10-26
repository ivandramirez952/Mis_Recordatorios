
package com.ivanramirez.misrecordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivanramirez.misrecordatorios.ui.theme.MisRecordatoriosTheme

// Modelos de datos inmutables
data class Categoria(
    val nombre: String
) {
    init {
        require(nombre.isNotBlank()) {
            "La categoria no puede estar vacia"
        }
    }
}

sealed class Prioridad(val etiqueta: String) {
    data object Alta : Prioridad("Alta")
    data object Media : Prioridad("Media")
    data object Baja : Prioridad("Baja")
}

sealed class Estado(val etiqueta: String) {
    data object Pendiente : Estado("Pendiente")
    data object Completado : Estado("Completado")
}

sealed class EstadoAplicacion {
    data object Cargando : EstadoAplicacion()
    data class Exito(val recordatorios: List<Recordatorio>) : EstadoAplicacion()
    data class Error(val mensaje: String) : EstadoAplicacion()
}

data class Recordatorio(
    val id: Int,
    val titulo: String,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: Estado,
    val descripcion: String? = null
) {
    init {
        require(id > 0) { "El ID debe ser positivo" }
        require(titulo.isNotBlank()) { "El titulo es obligatorio" }
        require(titulo.length <= 100) {
            "El titulo no puede superar los 100 caracteres"
        }
    }
}

// Categorias sin duplicados mediante Set
val categorias: Set<Categoria> = setOf(
    Categoria("Estudio"),
    Categoria("Trabajo"),
    Categoria("Personal"),
    Categoria("Estudio")
)

// Colores asociados a categorias mediante Map
val coloresCategorias: Map<String, Color> = mapOf(
    "Estudio" to Color(0xFF1565C0),
    "Trabajo" to Color(0xFF6A1B9A),
    "Personal" to Color(0xFF2E7D32)
)

// Datos simulados mediante List
val recordatoriosSimulados: List<Recordatorio> = listOf(
    Recordatorio(
        1,
        "Estudiar Kotlin",
        Categoria("Estudio"),
        Prioridad.Alta,
        Estado.Pendiente,
        "Repasar conceptos de programacion"
    ),
    Recordatorio(
        2,
        "Presentar informe",
        Categoria("Trabajo"),
        Prioridad.Alta,
        Estado.Pendiente
    ),
    Recordatorio(
        3,
        "Comprar alimentos",
        Categoria("Personal"),
        Prioridad.Media,
        Estado.Completado
    ),
    Recordatorio(
        4,
        "Practicar Jetpack Compose",
        Categoria("Estudio"),
        Prioridad.Media,
        Estado.Pendiente
    ),
    Recordatorio(
        5,
        "Organizar la semana",
        Categoria("Personal"),
        Prioridad.Baja,
        Estado.Pendiente
    )
)

// Ejemplos de filtrado por estado y prioridad
val recordatoriosPendientes = recordatoriosSimulados.filter {
    it.estado == Estado.Pendiente
}

val recordatoriosAltaPrioridad = recordatoriosSimulados.filter {
    it.prioridad == Prioridad.Alta
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            MisRecordatoriosTheme {
                val estado = EstadoAplicacion.Exito(
                    recordatoriosSimulados
                )

                PantallaRecordatorios(estado)
            }
        }
    }
}

@Composable
fun PantallaRecordatorios(estado: EstadoAplicacion) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Mis Recordatorios",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Organiza tus tareas y prioridades",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (estado) {
                is EstadoAplicacion.Cargando -> {
                    CircularProgressIndicator()
                }

                is EstadoAplicacion.Error -> {
                    Text(text = estado.mensaje)
                }

                is EstadoAplicacion.Exito -> {
                    Text(
                        text = "Total: ${estado.recordatorios.size} recordatorios",
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            estado.recordatorios,
                            key = { it.id }
                        ) { recordatorio ->
                            TarjetaRecordatorio(recordatorio)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaRecordatorio(recordatorio: Recordatorio) {
    val colorCategoria = coloresCategorias[
        recordatorio.categoria.nombre
    ] ?: Color.Gray

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recordatorio.titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Categoria: ${recordatorio.categoria.nombre}",
                color = colorCategoria,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Prioridad: ${recordatorio.prioridad.etiqueta}"
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Estado: ${recordatorio.estado.etiqueta}",
                color = if (recordatorio.estado == Estado.Completado)
                    Color(0xFF2E7D32)
                else
                    Color(0xFFE65100)
            )

            recordatorio.descripcion?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    fontSize = 12.sp
                )
            }
        }
    }
}
