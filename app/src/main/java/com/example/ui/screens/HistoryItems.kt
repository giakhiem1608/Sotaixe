package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntry
import com.example.data.RevenueEntry
import com.example.data.RevenueSource
import com.example.ui.theme.ExpenseError
import com.example.utils.FormatUtils

@Composable
fun RevenueEntryItem(entry: RevenueEntry, sources: List<RevenueSource>, onEdit: (RevenueEntry) -> Unit) {
    val source = sources.find { it.id == entry.sourceId }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clickable { onEdit(entry) }
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(source?.name ?: "Nguồn khác", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                var subtext = "${entry.trips} cuốc"
                if (entry.distanceKm != null) subtext += " • ${String.format("%.1f", entry.distanceKm).replace(".0", "").replace(".", ",")} km"
                if ((entry.tipAmount ?: 0L) > 0L) subtext += " • Tip +${FormatUtils.formatCurrency(entry.tipAmount ?: 0L)}"
                subtext += " • ${FormatUtils.formatTime(entry.timestamp)}"
                Text(subtext, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(FormatUtils.formatCurrency(entry.amount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    }
}

@Composable
fun ExpenseEntryItem(entry: ExpenseEntry, categories: List<ExpenseCategory>, onEdit: (ExpenseEntry) -> Unit) {
    val category = categories.find { it.id == entry.categoryId }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clickable { onEdit(entry) }
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(category?.name ?: "Khác", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(FormatUtils.formatTime(entry.timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("- " + FormatUtils.formatCurrency(entry.amount), fontWeight = FontWeight.Bold, color = ExpenseError)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    }
}
