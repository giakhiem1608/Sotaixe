package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revenue_sources")
data class RevenueSource(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,
    val isDefault: Boolean = false,
    val isActive: Boolean = true
)
