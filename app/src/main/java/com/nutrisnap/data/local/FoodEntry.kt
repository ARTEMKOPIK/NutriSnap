package com.nutrisnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dishName: String,
    val calories: Int,
    val proteins: Float,
    val fats: Float,
    val carbs: Float,
    val description: String,
    val aiTip: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String? = null
)
