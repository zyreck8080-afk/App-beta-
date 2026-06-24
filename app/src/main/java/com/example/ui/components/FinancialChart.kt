package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.Sale
import com.example.ui.theme.ProfitNegative
import com.example.ui.theme.ProfitPositive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyFinancials(
    val dateStr: String,
    val timestamp: Long,
    val earnings: Float,
    val expenses: Float,
    val profit: Float
)

@Composable
fun FinancialChart(sales: List<Sale>, modifier: Modifier = Modifier) {
    val dailyStats = remember(sales) {
        val format = SimpleDateFormat("dd/MM", Locale.getDefault())
        sales.groupBy {
            // Agrupar por día (truncando horas)
            val c = java.util.Calendar.getInstance()
            c.timeInMillis = it.timestamp
            c.set(java.util.Calendar.HOUR_OF_DAY, 0)
            c.set(java.util.Calendar.MINUTE, 0)
            c.set(java.util.Calendar.SECOND, 0)
            c.set(java.util.Calendar.MILLISECOND, 0)
            c.timeInMillis
        }.map { (timestamp, salesForDay) ->
            DailyFinancials(
                dateStr = format.format(Date(timestamp)),
                timestamp = timestamp,
                earnings = salesForDay.sumOf { it.salePriceTotal }.toFloat(),
                expenses = salesForDay.sumOf { it.totalExpenses }.toFloat(),
                profit = salesForDay.sumOf { it.netProfit }.toFloat()
            )
        }.sortedBy { it.timestamp }.takeLast(7)
    }

    CustomLineChart(dailyStats, modifier)
}

@Composable
private fun CustomLineChart(data: List<DailyFinancials>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay suficientes datos", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val maxAmount = remember(data) {
        val maxErt = data.maxOfOrNull { it.earnings } ?: 0f
        val maxExp = data.maxOfOrNull { it.expenses } ?: 0f
        maxOf(maxErt, maxExp, 100f) // Mantener escala mínima
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val negativeColor = ProfitNegative
    val positiveColor = ProfitPositive

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Tendencia Financiera (Últimos ${data.size} días)",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)) {
                val width = size.width
                val height = size.height - 20f // padding bottom for labels
                
                val stepX = if (data.size > 1) width / (data.size - 1) else width / 2
                val scaleY = height / (maxAmount * 1.1f) // 10% padding top

                val earningsPath = Path()
                val expensesPath = Path()
                val profitPath = Path()

                data.forEachIndexed { index, daily ->
                    val x = if (data.size == 1) width / 2 else index * stepX
                    val earningsY = height - (daily.earnings * scaleY)
                    val expensesY = height - (daily.expenses * scaleY)
                    val profitY = height - (daily.profit * scaleY)

                    if (index == 0) {
                        earningsPath.moveTo(x, earningsY)
                        expensesPath.moveTo(x, expensesY)
                        profitPath.moveTo(x, profitY)
                    } else {
                        earningsPath.lineTo(x, earningsY)
                        expensesPath.lineTo(x, expensesY)
                        profitPath.lineTo(x, profitY)
                    }

                    // Puntos de datos
                    drawCircle(color = primaryColor, radius = 6f, center = Offset(x, earningsY))
                    drawCircle(color = negativeColor, radius = 6f, center = Offset(x, expensesY))
                    drawCircle(color = positiveColor, radius = 6f, center = Offset(x, profitY))
                }

                drawPath(
                    path = earningsPath,
                    color = primaryColor,
                    style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = expensesPath,
                    color = negativeColor,
                    style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = profitPath,
                    color = positiveColor,
                    style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                LegendItem("Ingresos", primaryColor)
                LegendItem("Gastos", negativeColor)
                LegendItem("Ganancias", positiveColor)
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
            .size(10.dp)
            .background(color, RoundedCornerShape(50)))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
