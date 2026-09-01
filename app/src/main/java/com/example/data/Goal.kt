package com.example.data

import com.squareup.moshi.JsonClass
import androidx.room.Entity
import androidx.room.PrimaryKey
@JsonClass(generateAdapter = true)
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "REVENUE" or "NET_INCOME"
    val amount: Long,
    val monthString: String // Format: YYYY-MM
)
