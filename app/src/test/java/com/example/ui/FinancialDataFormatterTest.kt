package com.example.ui

import com.example.data.Product
import com.example.data.Sale
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialDataFormatterTest {

    @Test
    fun formatDataToJson_withEmptyLists_returnsCorrectJson() {
        val resultJsonString = FinancialDataFormatter.formatDataToJson(emptyList(), emptyList())
        val jsonElement = Json.parseToJsonElement(resultJsonString)
        val rootObject = jsonElement.jsonObject

        // Verify Catalog
        val catalog = rootObject["catalog"]?.jsonObject
        assertEquals(0, catalog?.get("totalProducts")?.jsonPrimitive?.int)
        assertEquals(0, catalog?.get("items")?.jsonArray?.size)

        // Verify Sales Summary
        val salesSummary = rootObject["salesSummary"]?.jsonObject
        assertEquals(0, salesSummary?.get("totalSalesCount")?.jsonPrimitive?.int)
        assertEquals(0.0, salesSummary?.get("totalIncome")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(0.0, salesSummary?.get("totalExpenses")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(0.0, salesSummary?.get("totalCostBase")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(0.0, salesSummary?.get("totalTransportExpenses")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(0.0, salesSummary?.get("totalFoodExpenses")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(0.0, salesSummary?.get("totalOtherExpenses")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(0.0, salesSummary?.get("netProfit")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(0, salesSummary?.get("transactions")?.jsonArray?.size)
    }

    @Test
    fun formatDataToJson_withData_returnsCorrectJson() {
        val products = listOf(
            Product(
                id = 1,
                name = "Test Product 1",
                costBase = 10.0,
                costExtra = 2.0,
                unit = "pcs",
                marginPercentage = 50.0
            ),
            Product(
                id = 2,
                name = "Test Product 2",
                costBase = 20.0,
                costExtra = 5.0,
                unit = "kg",
                marginPercentage = 20.0
            )
        )

        val sales = listOf(
            Sale(
                id = 1,
                productId = 1,
                productName = "Test Product 1",
                quantity = 2,
                salePriceTotal = 48.0,
                costBaseTotal = 24.0, // costBase 12 * 2
                transportExpense = 5.0,
                foodExpense = 0.0,
                otherExpense = 1.0,
                timestamp = 1672531200000L
            ),
            Sale(
                id = 2,
                productId = 2,
                productName = "Test Product 2",
                quantity = 1,
                salePriceTotal = 31.25,
                costBaseTotal = 25.0, // costBase 25 * 1
                transportExpense = 0.0,
                foodExpense = 2.0,
                otherExpense = 0.0,
                timestamp = 1672531200000L
            )
        )

        val resultJsonString = FinancialDataFormatter.formatDataToJson(products, sales)
        val jsonElement = Json.parseToJsonElement(resultJsonString)
        val rootObject = jsonElement.jsonObject

        // Verify Catalog
        val catalog = rootObject["catalog"]?.jsonObject
        assertEquals(2, catalog?.get("totalProducts")?.jsonPrimitive?.int)

        val items = catalog?.get("items")?.jsonArray
        assertEquals(2, items?.size)
        assertEquals(1, items?.get(0)?.jsonObject?.get("id")?.jsonPrimitive?.int)
        assertEquals("Test Product 1", items?.get(0)?.jsonObject?.get("name")?.jsonPrimitive?.content)
        assertEquals(10.0, items?.get(0)?.jsonObject?.get("costBase")?.jsonPrimitive?.double ?: -1.0, 0.001)

        // Verify Sales Summary
        val salesSummary = rootObject["salesSummary"]?.jsonObject
        assertEquals(2, salesSummary?.get("totalSalesCount")?.jsonPrimitive?.int)
        assertEquals(79.25, salesSummary?.get("totalIncome")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(57.0, salesSummary?.get("totalExpenses")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(49.0, salesSummary?.get("totalCostBase")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(5.0, salesSummary?.get("totalTransportExpenses")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(2.0, salesSummary?.get("totalFoodExpenses")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(1.0, salesSummary?.get("totalOtherExpenses")?.jsonPrimitive?.double ?: -1.0, 0.001)
        assertEquals(22.25, salesSummary?.get("netProfit")?.jsonPrimitive?.double ?: -1.0, 0.001)

        val transactions = salesSummary?.get("transactions")?.jsonArray
        assertEquals(2, transactions?.size)
        assertEquals(1, transactions?.get(0)?.jsonObject?.get("id")?.jsonPrimitive?.int)
        assertEquals(48.0, transactions?.get(0)?.jsonObject?.get("salePriceTotal")?.jsonPrimitive?.double ?: -1.0, 0.001)
    }

}
