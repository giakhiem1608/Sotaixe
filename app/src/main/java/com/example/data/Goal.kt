package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "REVENUE" or "NET_INCOME"
    val amount: Long,
    val monthString: String // Format: YYYY-MM
)
