package com.example.data

import com.squareup.moshi.JsonClass
import androidx.room.Entity
import androidx.room.PrimaryKey
@JsonClass(generateAdapter = true)
@Entity(tableName = "expense_categories")
data class ExpenseCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconName: String,
    val isDefault: Boolean = false,
    val isActive: Boolean = true
)
