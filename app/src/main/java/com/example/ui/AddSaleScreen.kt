package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Product
import com.example.data.Sale
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var expandedProduct by remember { mutableStateOf(false) }
    
    var quantity by remember { mutableStateOf("1") }
    var transportExpense by remember { mutableStateOf("0") }
    var foodExpense by remember { mutableStateOf("0") }
    var otherExpense by remember { mutableStateOf("0") }

    val qtyNum = quantity.toIntOrNull() ?: 0
    val tExp = transportExpense.toDoubleOrNull() ?: 0.0
    val fExp = foodExpense.toDoubleOrNull() ?: 0.0
    val oExp = otherExpense.toDoubleOrNull() ?: 0.0

    val salePriceTotal = (selectedProduct?.salePrice ?: 0.0) * qtyNum
    val netProfit = salePriceTotal - ((selectedProduct?.totalCost ?: 0.0) * qtyNum) - tExp - fExp - oExp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Venta", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            ExposedDropdownMenuBox(
                expanded = expandedProduct,
                onExpandedChange = { expandedProduct = !expandedProduct }
            ) {
                OutlinedTextField(
                    value = selectedProduct?.name ?: "Selecciona un producto",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Producto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProduct) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedProduct,
                    onDismissRequest = { expandedProduct = false }
                ) {
                    products.forEach { product ->
                        DropdownMenuItem(
                            text = { Text(product.name) },
                            onClick = {
                                selectedProduct = product
                                expandedProduct = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Cantidad vendida") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Gastos Operativos de esta venta", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = transportExpense,
                onValueChange = { transportExpense = it },
                label = { Text("Transporte (ej. pasajes combi)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = foodExpense,
                onValueChange = { foodExpense = it },
                label = { Text("Alimentos (ej. agua, comida)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = otherExpense,
                onValueChange = { otherExpense = it },
                label = { Text("Otros Gastos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Real-time Preview 
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen de Venta", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ingreso Total:")
                        Text(String.format(Locale.getDefault(), "$%.2f", salePriceTotal))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Costo Producto + Gastos:")
                        Text(String.format(Locale.getDefault(), "$%.2f", ((selectedProduct?.totalCost ?: 0.0) * qtyNum) + tExp + fExp + oExp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ganancia Neta Final:", fontWeight = FontWeight.SemiBold)
                        Text(
                            String.format(Locale.getDefault(), "$%.2f", netProfit),
                            color = if (netProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedProduct != null && qtyNum > 0) {
                        viewModel.addSale(
                            Sale(
                                productId = selectedProduct!!.id,
                                productName = selectedProduct!!.name,
                                quantity = qtyNum,
                                salePriceTotal = salePriceTotal,
                                costBaseTotal = selectedProduct!!.totalCost * qtyNum,
                                transportExpense = tExp,
                                foodExpense = fExp,
                                otherExpense = oExp
                            )
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedProduct != null && qtyNum > 0,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Registrar Venta", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
