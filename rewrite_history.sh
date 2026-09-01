cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/HistoryScreen.kt
package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntry
import com.example.data.RevenueEntry
import com.example.data.RevenueSource
import com.example.ui.theme.ExpenseError
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

@Composable
fun HistoryScreen(viewModel: LedgerViewModel) {
    val revenueEntries by viewModel.revenueEntries.collectAsState()
    val expenseEntries by viewModel.expenseEntries.collectAsState()
    val sources by viewModel.activeRevenueSources.collectAsState()
    val categories by viewModel.activeExpenseCategories.collectAsState()
    
    var editingRevenue by remember { mutableStateOf<RevenueEntry?>(null) }
    var editingExpense by remember { mutableStateOf<ExpenseEntry?>(null) }

    val allDates = (revenueEntries.map { it.dateString } + expenseEntries.map { it.dateString }).distinct().sortedDescending()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Lịch sử giao dịch",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        )

        if (allDates.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Chưa có giao dịch nào",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(allDates) { dateStr ->
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
    sources: List<RevenueSource>,
    categories: List<ExpenseCategory>,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 0.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = displayDate, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
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

            Spacer(modifier = Modifier.height(12.dp))
            
            // Summary of sources
            val sourceMap = revenues.groupBy { it.sourceId }
            sourceMap.forEach { (sourceId, revs) ->
                val source = sources.find { it.id == sourceId }
                val sum = revs.sumOf { it.amount }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(source?.name ?: "Khác", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(FormatUtils.formatCurrency(sum), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            if (totalExp > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tổng chi phí", style = MaterialTheme.typography.bodyMedium, color = ExpenseError)
                    Text("- " + FormatUtils.formatCurrency(totalExp), style = MaterialTheme.typography.bodyMedium, color = ExpenseError)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(if (expanded) "Thu gọn giao dịch" else "Xem giao dịch")
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Expanded Content (Individual Entries)
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    if (revenues.isNotEmpty()) {
                        Text("Doanh thu", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        revenues.sortedByDescending { it.id }.forEach { entry ->
                            RevenueEntryItem(entry, sources, onEditRevenue)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    if (expenses.isNotEmpty()) {
                        Text("Chi phí", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        expenses.sortedByDescending { it.id }.forEach { entry ->
                            ExpenseEntryItem(entry, categories, onEditExpense)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RevenueEntryItem(entry: RevenueEntry, sources: List<RevenueSource>, onEdit: (RevenueEntry) -> Unit) {
    val source = sources.find { it.id == entry.sourceId }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(source?.name ?: "Nguồn khác", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text("${entry.trips} cuốc • ${FormatUtils.formatTime(entry.timestamp)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(FormatUtils.formatCurrency(entry.amount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        IconButton(onClick = { onEdit(entry) }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = "Sửa", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ExpenseEntryItem(entry: ExpenseEntry, categories: List<ExpenseCategory>, onEdit: (ExpenseEntry) -> Unit) {
    val category = categories.find { it.id == entry.categoryId }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(category?.name ?: "Khác", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(FormatUtils.formatTime(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("- " + FormatUtils.formatCurrency(entry.amount), fontWeight = FontWeight.Bold, color = ExpenseError)
        IconButton(onClick = { onEdit(entry) }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = "Sửa", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
INNER_EOF
