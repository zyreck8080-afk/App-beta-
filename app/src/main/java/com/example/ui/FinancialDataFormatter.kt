package com.example.ui

import com.example.data.Product
import com.example.data.Sale
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.json.addJsonObject

object FinancialDataFormatter {
    fun formatDataToJson(products: List<Product>, sales: List<Sale>): String {
        val root = buildJsonObject {
            putJsonObject("catalog") {
                put("totalProducts", products.size)
                putJsonArray("items") {
                    products.forEach { p ->
                        addJsonObject {
                            put("id", p.id)
                            put("name", p.name)
                            put("costBase", p.costBase)
                            put("costExtra", p.costExtra)
                            put("marginPercentage", p.marginPercentage)
                            put("salePrice", p.salePrice)
                            put("profitNet", p.profitNet)
                        }
                    }
                }
            }
            putJsonObject("salesSummary") {
                put("totalSalesCount", sales.size)
                put("totalIncome", sales.sumOf { it.salePriceTotal })
                put("totalExpenses", sales.sumOf { it.totalExpenses })
                put("totalCostBase", sales.sumOf { it.costBaseTotal })
                put("totalTransportExpenses", sales.sumOf { it.transportExpense })
                put("totalFoodExpenses", sales.sumOf { it.foodExpense })
                put("totalOtherExpenses", sales.sumOf { it.otherExpense })
                put("netProfit", sales.sumOf { it.netProfit })
                putJsonArray("transactions") {
                    sales.forEach { s ->
                        addJsonObject {
                            put("id", s.id)
                            put("productName", s.productName)
                            put("quantity", s.quantity)
                            put("salePriceTotal", s.salePriceTotal)
                            put("costBaseTotal", s.costBaseTotal)
                            put("transportExpense", s.transportExpense)
                            put("foodExpense", s.foodExpense)
                            put("otherExpense", s.otherExpense)
                            put("netProfit", s.netProfit)
                            put("timestamp", s.timestamp)
                        }
                    }
                }
            }
        }
        return root.toString()
    }
}
