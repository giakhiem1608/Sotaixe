cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/TodayScreen.kt
package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.data.ExpenseCategory
import com.example.data.RevenueSource
import com.example.data.RevenueEntry
import com.example.ui.theme.*
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TodayScreen(viewModel: LedgerViewModel) {
    val currentDate by viewModel.currentDate.collectAsState()
    val totalRevenue by viewModel.todaysTotalRevenue.collectAsState()
    val totalExpense by viewModel.todaysTotalExpense.collectAsState()
    val totalTrips by viewModel.todaysTotalTrips.collectAsState()
    val totalDuration by viewModel.todaysTotalDuration.collectAsState()
    val totalDistance by viewModel.todaysTotalDistance.collectAsState()
    val netIncome by viewModel.todaysNetIncome.collectAsState()
    
    val revenueEntries by viewModel.todaysRevenueEntries.collectAsState()
    val sources by viewModel.activeRevenueSources.collectAsState()
    val categories by viewModel.activeExpenseCategories.collectAsState()
    
    var showAddRevenueSheet by remember { mutableStateOf(false) }
    var showAddExpenseSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Compact Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousDay() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Ngày trước")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = FormatUtils.formatDate(currentDate),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = FormatUtils.getDayOfWeek(currentDate).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { viewModel.nextDay() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ngày sau")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "THU NHẬP HÔM NAY",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = FormatUtils.formatCurrency(netIncome),
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 36.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Doanh thu", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(FormatUtils.formatCurrency(totalRevenue), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Chi phí", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(FormatUtils.formatCurrency(totalExpense), fontWeight = FontWeight.Bold, color = ExpenseError)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                
                val avgRevenue = if (totalTrips > 0) totalRevenue / totalTrips else 0L
                Text(
                    text = if (totalTrips > 0) "$totalTrips cuốc • TB ${FormatUtils.formatCurrency(avgRevenue)}/cuốc" else "Chưa có cuốc nào",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Quick Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { showAddExpenseSheet = true },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+ Chi phí", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { showAddRevenueSheet = true },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("+ Doanh thu", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Cơ cấu doanh thu",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        if (totalRevenue == 0L) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    "Chưa có dữ liệu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
        } else {
            val breakdown = sources.map { source ->
                val sourceRevenue = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.amount }
                val sourceTrips = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.trips }
                Triple(source, sourceRevenue, sourceTrips)
            }.filter { it.second > 0 }.sortedByDescending { it.second }
            
            // Stacked progress bar
            Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {
                breakdown.forEach { (source, amount, _) ->
                    val weight = amount.toFloat() / totalRevenue.toFloat()
                    val colorHex = source.colorHex.replace("#", "")
                    val color = Color(android.graphics.Color.parseColor("#$colorHex"))
                    Box(modifier = Modifier.weight(weight).fillMaxHeight().background(color))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(breakdown) { (source, amount, trips) ->
                    val percent = if (totalRevenue > 0) (amount.toFloat() / totalRevenue.toFloat()) * 100 else 0f
                    val colorHex = source.colorHex.replace("#", "")
                    val color = Color(android.graphics.Color.parseColor("#$colorHex"))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(6.dp)).background(color))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(source.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text("$trips cuốc • ${String.format("%.1f", percent)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(FormatUtils.formatCurrency(amount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
    
    if (showAddRevenueSheet) {
        AddRevenueSheet(
            sources = sources,
            onDismiss = { showAddRevenueSheet = false },
            onSave = { sourceId, amount, trips, dur, dist, note ->
                viewModel.addRevenue(sourceId, amount, trips, dur, dist, note)
                showAddRevenueSheet = false
            }
        )
    }
    
    if (showAddExpenseSheet) {
        AddExpenseSheet(
            categories = categories,
            onDismiss = { showAddExpenseSheet = false },
            onSave = { categoryId, amount, note ->
                viewModel.addExpense(categoryId, amount, note)
                showAddExpenseSheet = false
            }
        )
    }
}
INNER_EOF
cat app/src/main/java/com/example/ui/screens/TodayScreen.kt
