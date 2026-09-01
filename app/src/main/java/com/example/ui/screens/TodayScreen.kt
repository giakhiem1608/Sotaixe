package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
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
import com.example.data.ExpenseCategory
import com.example.data.RevenueSource
import com.example.ui.theme.*
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(16.dp)
    ) {
        // Date Header
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
                    text = FormatUtils.getDayOfWeek(currentDate).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = FormatUtils.formatDate(currentDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = { viewModel.nextDay() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ngày sau")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Net Income Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "THU NHẬP SAU CHI PHÍ",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = FormatUtils.formatCurrency(netIncome),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Doanh thu", style = MaterialTheme.typography.bodySmall)
                        Text(FormatUtils.formatCurrency(totalRevenue), fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Chi phí", style = MaterialTheme.typography.bodySmall)
                        Text(FormatUtils.formatCurrency(totalExpense), fontWeight = FontWeight.Bold, color = ColorExpense)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                
                val stats = mutableListOf<String>()
                if (totalTrips > 0) stats.add("$totalTrips cuốc")
                if (totalDuration > 0) stats.add("${String.format("%.1f", totalDuration)} giờ")
                if (totalDistance > 0) stats.add("${String.format("%.1f", totalDistance)} km")
                
                Text(
                    text = if (stats.isEmpty()) "Chưa có dữ liệu" else stats.joinToString(" • "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (totalRevenue > 0 && totalExpense > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val expPercent = (totalExpense.toFloat() / totalRevenue.toFloat()) * 100
                    Text(
                        text = "Chi phí / Doanh thu: ${String.format("%.1f", expPercent)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "CƠ CẤU DOANH THU",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Revenue Breakdown
        if (totalRevenue == 0L) {
            Text(
                "Chưa có dữ liệu doanh thu hôm nay",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                val breakdown = sources.map { source ->
                    val sourceRevenue = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.amount }
                    val sourceTrips = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.trips }
                    Triple(source, sourceRevenue, sourceTrips)
                }.filter { it.second > 0 }.sortedByDescending { it.second }

                items(breakdown) { (source, amount, trips) ->
                    val percent = if (totalRevenue > 0) (amount.toFloat() / totalRevenue.toFloat()) * 100 else 0f
                    RevenueBreakdownItem(source, amount, trips, percent)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f, fill = false))

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { showAddExpenseSheet = true },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("CHI PHÍ", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showAddRevenueSheet = true },
                modifier = Modifier
                    .weight(1.5f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("NHẬP DOANH THU", fontWeight = FontWeight.Bold)
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

@Composable
fun RevenueBreakdownItem(source: RevenueSource, amount: Long, trips: Int, percent: Float) {
    val color = try {
        Color(android.graphics.Color.parseColor(source.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(source.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "$trips cuốc • ${String.format("%.1f", percent)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                FormatUtils.formatCurrency(amount),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRevenueSheet(
    sources: List<RevenueSource>,
    onDismiss: () -> Unit,
    onSave: (Int, Long, Int, Float?, Float?, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var selectedSourceId by remember { mutableStateOf(sources.firstOrNull()?.id ?: 0) }
        var amountStr by remember { mutableStateOf("") }
        var tripsStr by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Nhập doanh thu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Source selector (simple row of buttons)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sources.forEach { source ->
                    FilterChip(
                        selected = selectedSourceId == source.id,
                        onClick = { selectedSourceId = source.id },
                        label = { Text(source.name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue ->
                    // Only allow digits
                    if (newValue.all { it.isDigit() }) {
                        amountStr = newValue
                    }
                },
                label = { Text("Số tiền thực nhận (đ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = tripsStr,
                onValueChange = { tripsStr = it },
                label = { Text("Số cuốc") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    val trips = tripsStr.toIntOrNull()
                    if (amount != null && trips != null && selectedSourceId != 0) {
                        onSave(selectedSourceId, amount, trips, null, null, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = amountStr.isNotBlank() && tripsStr.isNotBlank()
            ) {
                Text("LƯU DOANH THU", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    categories: List<ExpenseCategory>,
    onDismiss: () -> Unit,
    onSave: (Int, Long, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: 0) }
        var amountStr by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Nhập chi phí", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Expense Category selector
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.take(4).forEach { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.name) }
                    )
                }
            }
            if (categories.size > 4) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.drop(4).take(4).forEach { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { selectedCategoryId = category.id },
                            label = { Text(category.name) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue ->
                    // Only allow digits
                    if (newValue.all { it.isDigit() }) {
                        amountStr = newValue
                    }
                },
                label = { Text("Số tiền (đ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    if (amount != null && selectedCategoryId != 0) {
                        onSave(selectedCategoryId, amount, note)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = amountStr.isNotBlank()
            ) {
                Text("LƯU CHI PHÍ", fontWeight = FontWeight.Bold)
            }
        }
    }
}
