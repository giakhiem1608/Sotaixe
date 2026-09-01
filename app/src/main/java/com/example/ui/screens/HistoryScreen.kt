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
import java.util.Date

@Composable
fun HistoryScreen(viewModel: LedgerViewModel) {
    val currentMonthStr by viewModel.currentMonth.collectAsState()
    val revenueEntries by viewModel.historyRevenueEntries.collectAsState()
    val expenseEntries by viewModel.historyExpenseEntries.collectAsState()
    val sources by viewModel.activeRevenueSources.collectAsState()
    val categories by viewModel.activeExpenseCategories.collectAsState()

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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(datesWithData) { dateStr ->
                    val dayRevenues = revenueEntries.filter { it.dateString == dateStr }
                    val dayExpenses = expenseEntries.filter { it.dateString == dateStr }
                    DayHistoryCard(
                        dateString = dateStr,
                        revenues = dayRevenues,
                        expenses = dayExpenses,
                        sources = sources,
                        categories = categories
                    )
                }
            }
        }
    }
}

@Composable
fun DayHistoryCard(
    dateString: String,
    revenues: List<RevenueEntry>,
    expenses: List<ExpenseEntry>,
    sources: List<com.example.data.RevenueSource>,
    categories: List<com.example.data.ExpenseCategory>
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
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
                
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expanded Content
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                    
                    if (revenues.isNotEmpty()) {
                        Text("DOANH THU", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                        revenues.forEach { rev ->
                            val source = sources.find { it.id == rev.sourceId }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(source?.name ?: "Nguồn khác", style = MaterialTheme.typography.bodyMedium)
                                Text(FormatUtils.formatCurrency(rev.amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    if (expenses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("CHI PHÍ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                        expenses.forEach { exp ->
                            val cat = categories.find { it.id == exp.categoryId }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(cat?.name ?: "Khác", style = MaterialTheme.typography.bodyMedium)
                                Text("- ${FormatUtils.formatCurrency(exp.amount)}", style = MaterialTheme.typography.bodyMedium, color = ColorExpense)
                            }
                        }
                    }
                }
            }
        }
    }
}
