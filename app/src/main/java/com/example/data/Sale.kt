package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val productName: String, // Keeping a snapshot just in case product is deleted
    val quantity: Int,
    val salePriceTotal: Double,
    val costBaseTotal: Double, // Costo base of product * quantity
    val transportExpense: Double = 0.0,
    val foodExpense: Double = 0.0,
    val otherExpense: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {
    val totalExpenses: Double
        get() = costBaseTotal + transportExpense + foodExpense + otherExpense

    val netProfit: Double
        get() = salePriceTotal - totalExpenses
}
