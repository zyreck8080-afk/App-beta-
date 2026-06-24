package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Product
import com.example.data.Sale
import com.example.ui.theme.ProfitNegative
import com.example.ui.theme.ProfitPositive
import java.util.*

import androidx.compose.material.icons.filled.Settings

val motivationalMessages = listOf(
    "¡Hoy eres Fuego! 🔥",
    "Cada venta cuenta. ¡Sigue así! 💫",
    "Construyendo un imperio paso a paso. 👑",
    "¡Excelente trabajo el de hoy! 🌟",
    "Tus finanzas bajo control. ✨"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onAddProduct: () -> Unit,
    onAddSale: () -> Unit,
    onChat: () -> Unit,
    onSettings: () -> Unit
) {
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val sales by viewModel.allSales.collectAsStateWithLifecycle()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()
    val isMinimalistMode by viewModel.isMinimalistMode.collectAsStateWithLifecycle()
    val monthlyGoal by viewModel.monthlyGoal.collectAsStateWithLifecycle()

    val haptic = LocalHapticFeedback.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Catálogo", "Ventas")

    var searchQuery by remember { mutableStateOf("") }
    val isSearchActive = searchQuery.isNotBlank()

    val motivationalMessage = remember { motivationalMessages.random() }

    val filteredProducts = if (isSearchActive) {
        products.filter { it.name.contains(searchQuery, ignoreCase = true) }
    } else products

    val filteredSales = if (isSearchActive) {
        sales.filter { it.productName.contains(searchQuery, ignoreCase = true) }
    } else sales

    val totalInvestment = if (selectedTabIndex == 0) products.sumOf { it.totalCost } else sales.sumOf { it.totalExpenses }
    val totalProfit = if (selectedTabIndex == 0) products.sumOf { it.profitNet } else sales.sumOf { it.netProfit }
    val salesProfit = sales.sumOf { it.netProfit }

    fun displayMoney(amount: Double): String {
        return if (isPrivacyMode) "$****" else String.format(Locale.getDefault(), "$%.2f", amount)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (isMinimalistMode) {
                        Text("Lumina", style = MaterialTheme.typography.titleLarge)
                    } else {
                        Text("LuminaProfit", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.togglePrivacyMode() 
                    }) {
                        Icon(if (isPrivacyMode) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = "Ocultar Saldos", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onChat) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Hablar con Asistente", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (selectedTabIndex == 0) onAddProduct() else onAddSale() 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_item_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (!isMinimalistMode) {
                Text(
                    motivationalMessage,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (!isMinimalistMode) {
                // Goal Progress
                val progress = (salesProfit / monthlyGoal).toFloat().coerceIn(0f, 1f)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progreso Mensual", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${(progress * 100).toInt()}% de $${monthlyGoal.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Stats Card
                Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Text(if (selectedTabIndex == 0) "Proyección de Catálogo" else "Resumen Real de Ventas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (selectedTabIndex == 0) "Costo Base" else "Gastos Totales", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f))
                            Text(String.format(Locale.getDefault(), "$%.2f", totalInvestment), style = MaterialTheme.typography.titleLarge)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Ganancia Neta", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.7f))
                            val color = if (totalProfit >= 0) ProfitPositive else ProfitNegative
                            Text(
                                String.format(Locale.getDefault(), "$%.2f", totalProfit),
                                style = MaterialTheme.typography.titleLarge,
                                color = color
                            )
                        }
                    }
                }
            }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTabIndex == 0) {
                if (filteredProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if(isSearchActive) "No se encontraron productos." else "No hay productos. Añade el primero.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductCard(product = product, isPrivacyMode = isPrivacyMode, onDelete = { viewModel.deleteProduct(it) })
                        }
                    }
                }
            } else {
                if (filteredSales.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if(isSearchActive) "No se encontraron ventas." else "Aún no tienes ventas registradas.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (!isMinimalistMode && !isSearchActive) {
                            item {
                                com.example.ui.components.FinancialChart(sales = filteredSales)
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                        items(filteredSales, key = { "sale_${it.id}" }) { sale ->
                            SaleCard(sale = sale, isPrivacyMode = isPrivacyMode, onDelete = { viewModel.deleteSale(it) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, isPrivacyMode: Boolean, onDelete: (Product) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp))
                Text("Costo: ${if(isPrivacyMode) "$****" else String.format(Locale.getDefault(), "$%.2f", product.totalCost)} / ${product.unit}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                Text("Venta: ${if(isPrivacyMode) "$****" else String.format(Locale.getDefault(), "$%.2f", product.salePrice)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val isProfit = product.profitNet >= 0
                val profitColor = if (isProfit) ProfitPositive else ProfitNegative
                val icon = if (isProfit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = profitColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if(isPrivacyMode) "$****" else String.format(Locale.getDefault(), "$%.2f", product.profitNet),
                        color = profitColor,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(onClick = { onDelete(product) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f))
                }
            }
        }
    }
}

@Composable
fun SaleCard(sale: Sale, isPrivacyMode: Boolean, onDelete: (Sale) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(sale.productName, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp))
                Text("Cant: ${sale.quantity} | Ingreso: ${if(isPrivacyMode) "$****" else String.format(Locale.getDefault(), "$%.2f", sale.salePriceTotal)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                Text("Gastos Op (Pasajes, otros): ${if(isPrivacyMode) "$****" else String.format(Locale.getDefault(), "$%.2f", sale.totalExpenses - sale.costBaseTotal)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                val isProfit = sale.netProfit >= 0
                val profitColor = if (isProfit) ProfitPositive else ProfitNegative
                val icon = if (isProfit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = profitColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if(isPrivacyMode) "$****" else String.format(Locale.getDefault(), "$%.2f", sale.netProfit),
                        color = profitColor,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(onClick = { onDelete(sale) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f))
                }
            }
        }
    }
}
