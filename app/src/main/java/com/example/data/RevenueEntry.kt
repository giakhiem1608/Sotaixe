package com.example.data

import com.squareup.moshi.JsonClass
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@JsonClass(generateAdapter = true)
@Entity(
    tableName = "revenue_entries",
    foreignKeys = [
        ForeignKey(
            entity = RevenueSource::class,
            parentColumns = ["id"],
            childColumns = ["sourceId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("sourceId"), Index("dateString")]
)
data class RevenueEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceId: Int,
    val amount: Long,
    val tipAmount: Long? = 0L,
    val trips: Int,
    val durationHrs: Float? = null,
    val distanceKm: Float? = null,
    val note: String = "",
    val timestamp: Long,
    val dateString: String // Format: YYYY-MM-DD
)
