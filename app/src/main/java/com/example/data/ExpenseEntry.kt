package com.example.data

import com.squareup.moshi.JsonClass
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@JsonClass(generateAdapter = true)
@Entity(
    tableName = "expense_entries",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId"), Index("dateString")]
)
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val amount: Long,
    val note: String = "",
    val timestamp: Long,
    val dateString: String // Format: YYYY-MM-DD
)
