cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/HistoryScreenFilter.kt
package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntry
import com.example.data.RevenueEntry
import com.example.data.RevenueSource

@Composable
fun FilterableTransactionList(
    revenues: List<RevenueEntry>,
    expenses: List<ExpenseEntry>,
    sources: List<RevenueSource>,
    categories: List<ExpenseCategory>,
    onEditRevenue: (RevenueEntry) -> Unit,
    onEditExpense: (ExpenseEntry) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filterOptions = mutableListOf("Tất cả")
    val presentSources = revenues.mapNotNull { r -> sources.find { it.id == r.sourceId }?.name }.distinct()
    filterOptions.addAll(presentSources)
    if (expenses.isNotEmpty()) {
        filterOptions.add("Chi phí")
    }

    val filteredRevenues = if (selectedFilter == "Tất cả") revenues else if (selectedFilter == "Chi phí") emptyList() else revenues.filter { r -> sources.find { it.id == r.sourceId }?.name == selectedFilter }
    val filteredExpenses = if (selectedFilter == "Tất cả" || selectedFilter == "Chi phí") expenses else emptyList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        if (filterOptions.size > 1) {
            ScrollableTabRow(
                selectedTabIndex = filterOptions.indexOf(selectedFilter),
                modifier = Modifier.padding(bottom = 12.dp).height(40.dp),
                edgePadding = 0.dp,
                indicator = {}, 
                divider = {},
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            ) {
                filterOptions.forEachIndexed { index, title ->
                    val selected = selectedFilter == title
                    val bgColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = bgColor,
                        border = if (!selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.clickable { selectedFilter = title }.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = contentColor
                        )
                    }
                }
            }
        }

        if (filteredRevenues.isNotEmpty()) {
            Text("Doanh thu", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            filteredRevenues.sortedByDescending { it.id }.forEach { entry ->
                RevenueEntryItem(entry, sources, onEditRevenue)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (filteredExpenses.isNotEmpty()) {
            Text("Chi phí", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            filteredExpenses.sortedByDescending { it.id }.forEach { entry ->
                ExpenseEntryItem(entry, categories, onEditExpense)
            }
        }
        
        if (filteredRevenues.isEmpty() && filteredExpenses.isEmpty()) {
            Text("Không có giao dịch", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
        }
    }
}
INNER_EOF

# Replace AnimatedVisibility content in HistoryScreen.kt
sed -i '/AnimatedVisibility(visible = expanded) {/,/            }        }    }}/c\
            AnimatedVisibility(visible = expanded) {\n                FilterableTransactionList(revenues, expenses, sources, categories, onEditRevenue, onEditExpense)\n            }\n        }\n    }\n}' app/src/main/java/com/example/ui/screens/HistoryScreen.kt
