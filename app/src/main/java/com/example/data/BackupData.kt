package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BackupData(
    val sources: List<RevenueSource>,
    val categories: List<ExpenseCategory>,
    val revenueEntries: List<RevenueEntry>,
    val expenseEntries: List<ExpenseEntry>,
    val goals: List<Goal>
)
