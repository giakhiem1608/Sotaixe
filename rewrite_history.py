import re

content = """package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntry
import com.example.data.RevenueEntry
import com.example.data.RevenueSource
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

@Composable
fun HistoryScreen(viewModel: LedgerViewModel) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val revenueEntries by viewModel.historyRevenueEntries.collectAsState()
    val expenseEntries by viewModel.historyExpenseEntries.collectAsState()
    val sources by viewModel.allRevenueSources.collectAsState()
    val categories by viewModel.allExpenseCategories.collectAsState()
    
    val missingKmFilterActive by viewModel.missingKmFilterActive.collectAsState()

    var selectedFilter by remember { mutableStateOf<String?>("ALL") }
    
    // Override if global missingKm filter is active
    LaunchedEffect(missingKmFilterActive) {
        if (missingKmFilterActive) {
            selectedFilter = "MISSING_KM"
        }
    }

    // Prepare data
    val allItems = mutableListOf<Any>()
    allItems.addAll(revenueEntries)
    allItems.addAll(expenseEntries)
    
    // Sort by timestamp descending
    allItems.sortByDescending { 
        when (it) {
            is RevenueEntry -> it.timestamp
            is ExpenseEntry -> it.timestamp
            else -> 0L
        }
    }
    
    // Apply Filter
    val filteredItems = allItems.filter { item ->
        when (selectedFilter) {
            "ALL" -> true
            "MISSING_KM" -> (item as? RevenueEntry)?.distanceKm == null
            "EXPENSE" -> item is ExpenseEntry
            else -> {
                // Must be a source filter
                if (item is RevenueEntry) {
                    val src = sources.find { it.id == item.sourceId }
                    src?.name == selectedFilter
                } else false
            }
        }
    }
    
    // Group by Date
    val groupedItems = filteredItems.groupBy { item ->
        when (item) {
            is RevenueEntry -> item.dateString
            is ExpenseEntry -> item.dateString
            else -> ""
        }
    }

    var selectedRevenue by remember { mutableStateOf<RevenueEntry?>(null) }
    var selectedExpense by remember { mutableStateOf<ExpenseEntry?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            Text(FormatUtils.formatDisplayMonth(currentMonth).uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { viewModel.nextMonth() }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null) }
        }

        // Filters
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { 
                        selectedFilter = "ALL"
                        viewModel.clearMissingKmFilter()
                    },
                    label = { Text("Tất cả") }
                )
            }
            
            val missingKmCount = revenueEntries.count { it.distanceKm == null }
            if (missingKmCount > 0) {
                item {
                    FilterChip(
                        selected = selectedFilter == "MISSING_KM",
                        onClick = { 
                            selectedFilter = "MISSING_KM"
                            viewModel.activateMissingKmFilter()
                        },
                        label = { Text("Thiếu KM ($missingKmCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFEF3C7),
                            selectedLabelColor = Color(0xFFD97706)
                        )
                    )
                }
            }
            
            val usedSources = revenueEntries.map { it.sourceId }.distinct()
            usedSources.forEach { srcId ->
                val src = sources.find { it.id == srcId }
                if (src != null) {
                    val count = revenueEntries.count { it.sourceId == srcId }
                    item {
                        FilterChip(
                            selected = selectedFilter == src.name,
                            onClick = { 
                                selectedFilter = src.name 
                                viewModel.clearMissingKmFilter()
                            },
                            label = { Text("${src.name} ($count)") }
                        )
                    }
                }
            }
            
            if (expenseEntries.isNotEmpty()) {
                item {
                    FilterChip(
                        selected = selectedFilter == "EXPENSE",
                        onClick = { 
                            selectedFilter = "EXPENSE"
                            viewModel.clearMissingKmFilter()
                        },
                        label = { Text("Chi phí (${expenseEntries.size})") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedItems.forEach { (dateStr, itemsForDate) ->
                item {
                    Text(
                        text = FormatUtils.formatDisplayDate(FormatUtils.parseDbDate(dateStr)),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                    )
                }
                
                items(itemsForDate) { item ->
                    when (item) {
                        is RevenueEntry -> {
                            val source = sources.find { it.id == item.sourceId }
                            RevenueRow(item, source) { selectedRevenue = item }
                        }
                        is ExpenseEntry -> {
                            val cat = categories.find { it.id == item.categoryId }
                            ExpenseRow(item, cat) { selectedExpense = item }
                        }
                    }
                }
            }
        }
    }

    if (selectedRevenue != null) {
        // Implement EditRevenueSheet
    }

    if (selectedExpense != null) {
        // Implement EditExpenseSheet
    }
}

@Composable
fun RevenueRow(entry: RevenueEntry, source: RevenueSource?, onClick: () -> Unit) {
    val color = try { Color(android.graphics.Color.parseColor(source?.colorHex ?: "#CCCCCC")) } catch(e: Exception) { Color.Gray }
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(source?.name ?: "Khác", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(2.dp))
                
                val timeStr = FormatUtils.formatTime(entry.timestamp)
                if (entry.distanceKm == null) {
                    Text("${entry.trips} cuốc • Chưa có KM • $timeStr", style = MaterialTheme.typography.bodySmall, color = Color(0xFFD97706))
                } else {
                    Text("${entry.trips} cuốc • ${String.format("%.1f", entry.distanceKm).replace(".", ",")} km • $timeStr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                if (entry.note.isNotEmpty()) {
                    Text(entry.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("+${FormatUtils.formatCurrency(entry.amount)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyLarge)
                if ((entry.tipAmount ?: 0L) > 0L) {
                    Text("Tip +${FormatUtils.formatCurrency(entry.tipAmount ?: 0L)}", color = Color(0xFFD97706), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ExpenseRow(entry: ExpenseEntry, category: ExpenseCategory?, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(category?.name ?: "Khác", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(2.dp))
                Text(FormatUtils.formatTime(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (entry.note.isNotEmpty()) {
                    Text(entry.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
            }
            Text("-${FormatUtils.formatCurrency(entry.amount)}", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content)
