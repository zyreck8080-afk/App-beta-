package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val costBase: Double,
    val costExtra: Double,
    val unit: String,
    val marginPercentage: Double = 30.0,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {

    val totalCost: Double
        get() = costBase + costExtra

    val salePrice: Double
        get() {
            if (marginPercentage >= 100.0) return totalCost * 2 // Arbitrary fallback if margin >= 100
            return totalCost / (1 - (marginPercentage / 100))
        }

    val profitNet: Double
        get() = salePrice - totalCost
}
