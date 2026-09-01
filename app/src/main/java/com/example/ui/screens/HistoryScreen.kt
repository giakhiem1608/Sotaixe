package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseEntry
import com.example.data.RevenueEntry
import com.example.ui.theme.ColorExpense
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

@Composable
fun HistoryScreen(viewModel: LedgerViewModel) {
    val currentMonthStr by viewModel.currentMonth.collectAsState()
    val revenueEntries by viewModel.historyRevenueEntries.collectAsState()
    val expenseEntries by viewModel.historyExpenseEntries.collectAsState()
    val sources by viewModel.activeRevenueSources.collectAsState()
    val categories by viewModel.activeExpenseCategories.collectAsState()

    var editingRevenue by remember { mutableStateOf<RevenueEntry?>(null) }
    var editingExpense by remember { mutableStateOf<ExpenseEntry?>(null) }

    // Format current month for display
    val displayMonth = remember(currentMonthStr) {
        val date = FormatUtils.parseDbMonth(currentMonthStr)
        if (date != null) FormatUtils.formatMonth(date.time) else currentMonthStr
    }

    // Group entries by date
    val datesWithData = remember(revenueEntries, expenseEntries) {
        val dates = mutableSetOf<String>()
        dates.addAll(revenueEntries.map { it.dateString })
        dates.addAll(expenseEntries.map { it.dateString })
        dates.sortedDescending()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Month Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tháng trước")
            }
            Text(
                text = "THÁNG $displayMonth",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.nextMonth() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Tháng sau")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (datesWithData.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Chưa có dữ liệu trong tháng này",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(datesWithData) { dateStr ->
                    val dayRevenues = revenueEntries.filter { it.dateString == dateStr }
                    val dayExpenses = expenseEntries.filter { it.dateString == dateStr }
                    DayHistoryCard(
                        dateString = dateStr,
                        revenues = dayRevenues,
                        expenses = dayExpenses,
                        sources = sources,
                        categories = categories,
                        onEditRevenue = { editingRevenue = it },
                        onEditExpense = { editingExpense = it }
                    )
                }
            }
        }
    }

    editingRevenue?.let { entry ->
        EditRevenueSheet(
            entry = entry,
            sources = sources,
            onDismiss = { editingRevenue = null },
            onSave = { updatedEntry ->
                viewModel.updateRevenueEntry(updatedEntry)
                editingRevenue = null
            },
            onDelete = {
                viewModel.deleteRevenue(entry.id)
                editingRevenue = null
            }
        )
    }

    editingExpense?.let { entry ->
        EditExpenseSheet(
            entry = entry,
            categories = categories,
            onDismiss = { editingExpense = null },
            onSave = { updatedEntry ->
                viewModel.updateExpenseEntry(updatedEntry)
                editingExpense = null
            },
            onDelete = {
                viewModel.deleteExpense(entry.id)
                editingExpense = null
            }
        )
    }
}

@Composable
fun DayHistoryCard(
    dateString: String,
    revenues: List<RevenueEntry>,
    expenses: List<ExpenseEntry>,
    sources: List<com.example.data.RevenueSource>,
    categories: List<com.example.data.ExpenseCategory>,
    onEditRevenue: (RevenueEntry) -> Unit,
    onEditExpense: (ExpenseEntry) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val totalRev = revenues.sumOf { it.amount }
    val totalExp = expenses.sumOf { it.amount }
    val netIncome = totalRev - totalExp
    val totalTrips = revenues.sumOf { it.trips }

    val dateTimestamp = FormatUtils.parseDbDate(dateString)
    val displayDate = FormatUtils.formatDate(dateTimestamp)
    val dayOfWeek = FormatUtils.getDayOfWeek(dateTimestamp)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = displayDate, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = dayOfWeek, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = FormatUtils.formatCurrency(netIncome),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$totalTrips cuốc",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Summary by Source
            if (revenues.isNotEmpty()) {
                val breakdown = sources.map { source ->
                    val sourceRevenues = revenues.filter { it.sourceId == source.id }
                    val amount = sourceRevenues.sumOf { it.amount }
                    val trips = sourceRevenues.sumOf { it.trips }
                    Triple(source, amount, trips)
                }.filter { it.second > 0 }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    breakdown.forEach { (source, amount, trips) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(source.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("$trips cuốc", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(FormatUtils.formatCurrency(amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Button "Xem giao dịch"
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expanded) "Thu gọn giao dịch" else "Xem giao dịch")
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Expanded Content (Individual Entries)
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp)
                ) {
                    if (revenues.isNotEmpty()) {
                        Text("CHI TIẾT DOANH THU", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
                        revenues.forEach { rev ->
                            val source = sources.find { it.id == rev.sourceId }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEditRevenue(rev) }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(source?.name ?: "Nguồn khác", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    if (rev.note.isNotBlank()) {
                                        Text(rev.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(FormatUtils.formatCurrency(rev.amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("${rev.trips} cuốc", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                    
                    if (expenses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("CHI TIẾT CHI PHÍ", style = MaterialTheme.typography.labelSmall, color = ColorExpense, modifier = Modifier.padding(bottom = 8.dp))
                        expenses.forEach { exp ->
                            val cat = categories.find { it.id == exp.categoryId }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEditExpense(exp) }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cat?.name ?: "Khác", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    if (exp.note.isNotBlank()) {
                                        Text(exp.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Text("- ${FormatUtils.formatCurrency(exp.amount)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ColorExpense)
                            }
                        }
                    }
                }
            }
        }
    }
}
