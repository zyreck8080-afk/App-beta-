package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val currentColor by viewModel.primaryColor.collectAsStateWithLifecycle()
    val currentType by viewModel.typographyStyle.collectAsStateWithLifecycle()

    val colors = listOf(
        "#FFFFB6C1" to "Rosa (Default)",
        "#FFB6C1" to "Rosa Intenso",
        "#FFDAB9" to "Durazno",
        "#E6E6FA" to "Lavanda",
        "#98FB98" to "Menta",
        "#87CEEB" to "Cielo"
    )

    val typographyStyles = listOf("Serif", "SansSerif", "Monospace")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personalización") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Color de Acento", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                colors.forEach { (hex, name) ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(hex)))
                            .clickable { viewModel.updatePrimaryColor(hex) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Tipografía", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            typographyStyles.forEach { style ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateTypographyStyle(style) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentType == style,
                        onClick = { viewModel.updateTypographyStyle(style) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(style, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Ahorro de Batería (Dark Mode)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            val isOledMode by viewModel.isOledMode.collectAsStateWithLifecycle()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo Oscuro OLED (Negro Puro)", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isOledMode,
                    onCheckedChange = { viewModel.toggleOledMode() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Modo Enfoque", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            val isMinimalistMode by viewModel.isMinimalistMode.collectAsStateWithLifecycle()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo Súper Minimalista\n(Oculta gráficas y metas)", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = isMinimalistMode,
                    onCheckedChange = { viewModel.toggleMinimalistMode() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Metas Financieras", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            val monthlyGoal by viewModel.monthlyGoal.collectAsStateWithLifecycle()
            var monthlyGoalStr by remember { mutableStateOf(monthlyGoal.toInt().toString()) }

            OutlinedTextField(
                value = monthlyGoalStr,
                onValueChange = {
                    monthlyGoalStr = it
                    it.toFloatOrNull()?.let { goal -> viewModel.updateMonthlyGoal(goal) }
                },
                label = { Text("Meta de Ganancia Mensual ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
