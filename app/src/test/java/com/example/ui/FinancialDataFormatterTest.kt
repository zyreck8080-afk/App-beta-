package com.example.ui

import com.example.data.Product
import com.example.data.Sale
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialDataFormatterTest {

    @Test
    fun `formatDataToJson handles empty lists correctly`() {
        val result = FinancialDataFormatter.formatDataToJson(emptyList(), emptyList())
        val json = Json.parseToJsonElement(result).jsonObject

        val catalog = json["catalog"]?.jsonObject
        assertEquals(0, catalog?.get("totalProducts")?.jsonPrimitive?.int)
        assertEquals(0, catalog?.get("items")?.jsonArray?.size)

        val salesSummary = json["salesSummary"]?.jsonObject
        assertEquals(0, salesSummary?.get("totalSalesCount")?.jsonPrimitive?.int)
        assertEquals(0.0, salesSummary?.get("totalIncome")?.jsonPrimitive?.double)
        assertEquals(0.0, salesSummary?.get("totalExpenses")?.jsonPrimitive?.double)
        assertEquals(0.0, salesSummary?.get("totalCostBase")?.jsonPrimitive?.double)
        assertEquals(0.0, salesSummary?.get("totalTransportExpenses")?.jsonPrimitive?.double)
        assertEquals(0.0, salesSummary?.get("totalFoodExpenses")?.jsonPrimitive?.double)
        assertEquals(0.0, salesSummary?.get("totalOtherExpenses")?.jsonPrimitive?.double)
        assertEquals(0.0, salesSummary?.get("netProfit")?.jsonPrimitive?.double)
        assertEquals(0, salesSummary?.get("transactions")?.jsonArray?.size)
    }

    @Test
    fun `formatDataToJson formats single items correctly`() {
        val product = Product(
            id = 1,
            name = "Test Product",
            costBase = 10.0,
            costExtra = 2.0,
            unit = "pcs",
            marginPercentage = 25.0
        )
        // totalCost = 12.0
        // salePrice = 12.0 / (1 - 0.25) = 16.0
        // profitNet = 16.0 - 12.0 = 4.0

        val sale = Sale(
            id = 100,
            productId = 1,
            productName = "Test Product",
            quantity = 2,
            salePriceTotal = 32.0,
            costBaseTotal = 24.0,
            transportExpense = 1.0,
            foodExpense = 0.5,
            otherExpense = 0.5,
            timestamp = 1600000000000L
        )
        // totalExpenses = 24.0 + 1.0 + 0.5 + 0.5 = 26.0
        // netProfit = 32.0 - 26.0 = 6.0

        val result = FinancialDataFormatter.formatDataToJson(listOf(product), listOf(sale))
        val json = Json.parseToJsonElement(result).jsonObject

        val catalog = json["catalog"]?.jsonObject
        assertEquals(1, catalog?.get("totalProducts")?.jsonPrimitive?.int)

        val productJson = catalog?.get("items")?.jsonArray?.get(0)?.jsonObject
        assertEquals(1, productJson?.get("id")?.jsonPrimitive?.int)
        assertEquals("Test Product", productJson?.get("name")?.jsonPrimitive?.content)
        assertEquals(10.0, productJson?.get("costBase")?.jsonPrimitive?.double)
        assertEquals(2.0, productJson?.get("costExtra")?.jsonPrimitive?.double)
        assertEquals(25.0, productJson?.get("marginPercentage")?.jsonPrimitive?.double)
        assertEquals(16.0, productJson?.get("salePrice")?.jsonPrimitive?.double)
        assertEquals(4.0, productJson?.get("profitNet")?.jsonPrimitive?.double)

        val salesSummary = json["salesSummary"]?.jsonObject
        assertEquals(1, salesSummary?.get("totalSalesCount")?.jsonPrimitive?.int)
        assertEquals(32.0, salesSummary?.get("totalIncome")?.jsonPrimitive?.double)
        assertEquals(26.0, salesSummary?.get("totalExpenses")?.jsonPrimitive?.double)
        assertEquals(24.0, salesSummary?.get("totalCostBase")?.jsonPrimitive?.double)
        assertEquals(1.0, salesSummary?.get("totalTransportExpenses")?.jsonPrimitive?.double)
        assertEquals(0.5, salesSummary?.get("totalFoodExpenses")?.jsonPrimitive?.double)
        assertEquals(0.5, salesSummary?.get("totalOtherExpenses")?.jsonPrimitive?.double)
        assertEquals(6.0, salesSummary?.get("netProfit")?.jsonPrimitive?.double)

        val saleJson = salesSummary?.get("transactions")?.jsonArray?.get(0)?.jsonObject
        assertEquals(100, saleJson?.get("id")?.jsonPrimitive?.int)
        assertEquals("Test Product", saleJson?.get("productName")?.jsonPrimitive?.content)
        assertEquals(2, saleJson?.get("quantity")?.jsonPrimitive?.int)
        assertEquals(32.0, saleJson?.get("salePriceTotal")?.jsonPrimitive?.double)
        assertEquals(24.0, saleJson?.get("costBaseTotal")?.jsonPrimitive?.double)
        assertEquals(1.0, saleJson?.get("transportExpense")?.jsonPrimitive?.double)
        assertEquals(0.5, saleJson?.get("foodExpense")?.jsonPrimitive?.double)
        assertEquals(0.5, saleJson?.get("otherExpense")?.jsonPrimitive?.double)
        assertEquals(6.0, saleJson?.get("netProfit")?.jsonPrimitive?.double)
        assertEquals(1600000000000L, saleJson?.get("timestamp")?.jsonPrimitive?.content?.toLong())
    }

    @Test
    fun `formatDataToJson formats multiple items correctly`() {
        val p1 = Product(id = 1, name = "P1", costBase = 10.0, costExtra = 0.0, unit = "kg", marginPercentage = 50.0) // SP=20.0
        val p2 = Product(id = 2, name = "P2", costBase = 20.0, costExtra = 0.0, unit = "kg", marginPercentage = 50.0) // SP=40.0

        val s1 = Sale(id = 101, productId = 1, productName = "P1", quantity = 1, salePriceTotal = 20.0, costBaseTotal = 10.0, transportExpense = 1.0)
        val s2 = Sale(id = 102, productId = 2, productName = "P2", quantity = 2, salePriceTotal = 80.0, costBaseTotal = 40.0, foodExpense = 5.0)

        val result = FinancialDataFormatter.formatDataToJson(listOf(p1, p2), listOf(s1, s2))
        val json = Json.parseToJsonElement(result).jsonObject

        val catalog = json["catalog"]?.jsonObject
        assertEquals(2, catalog?.get("totalProducts")?.jsonPrimitive?.int)
        assertEquals(2, catalog?.get("items")?.jsonArray?.size)

        val salesSummary = json["salesSummary"]?.jsonObject
        assertEquals(2, salesSummary?.get("totalSalesCount")?.jsonPrimitive?.int)
        // totalIncome = 20.0 + 80.0 = 100.0
        assertEquals(100.0, salesSummary?.get("totalIncome")?.jsonPrimitive?.double)
        // totalCostBase = 10.0 + 40.0 = 50.0
        assertEquals(50.0, salesSummary?.get("totalCostBase")?.jsonPrimitive?.double)
        // totalTransport = 1.0 + 0.0 = 1.0
        assertEquals(1.0, salesSummary?.get("totalTransportExpenses")?.jsonPrimitive?.double)
        // totalFood = 0.0 + 5.0 = 5.0
        assertEquals(5.0, salesSummary?.get("totalFoodExpenses")?.jsonPrimitive?.double)
        // totalOther = 0.0 + 0.0 = 0.0
        assertEquals(0.0, salesSummary?.get("totalOtherExpenses")?.jsonPrimitive?.double)
        // totalExpenses = 50.0 + 1.0 + 5.0 = 56.0
        assertEquals(56.0, salesSummary?.get("totalExpenses")?.jsonPrimitive?.double)
        // netProfit = 100.0 - 56.0 = 44.0
        assertEquals(44.0, salesSummary?.get("netProfit")?.jsonPrimitive?.double)

        assertEquals(2, salesSummary?.get("transactions")?.jsonArray?.size)
    }
}
