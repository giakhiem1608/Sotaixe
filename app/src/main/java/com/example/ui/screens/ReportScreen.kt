package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseError
import com.example.ui.theme.ColorGrab
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

@Composable
fun ReportScreen(viewModel: LedgerViewModel) {
    val currentMonthStr by viewModel.currentMonth.collectAsState()
    val revenueEntries by viewModel.historyRevenueEntries.collectAsState()
    val expenseEntries by viewModel.historyExpenseEntries.collectAsState()
    val sources by viewModel.activeRevenueSources.collectAsState()
    
    val displayMonth = remember(currentMonthStr) {
        val date = FormatUtils.parseDbMonth(currentMonthStr)
        if (date != null) FormatUtils.formatMonth(date.time) else currentMonthStr
    }
    
    val totalRev = revenueEntries.sumOf { it.amount }
    val totalExp = expenseEntries.sumOf { it.amount }
    val netIncome = totalRev - totalExp
    val totalTrips = revenueEntries.sumOf { it.trips }
    val daysWorked = revenueEntries.map { it.dateString }.distinct().size
    
    var showMonthPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tháng trước")
            }
            TextButton(onClick = { showMonthPicker = true }) {
                Text(
                    text = "BÁO CÁO THÁNG $displayMonth",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = { viewModel.nextMonth() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Tháng sau")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (totalRev == 0L && totalExp == 0L) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Chưa có dữ liệu để báo cáo", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Goals Card
                item {
                    val goal by viewModel.currentMonthGoal.collectAsState()
                    var showGoalDialog by remember { mutableStateOf(false) }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { showGoalDialog = true },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mục tiêu tháng $displayMonth", fontWeight = FontWeight.Bold)
                                Icon(Icons.Filled.Edit, contentDescription = "Sửa mục tiêu", modifier = Modifier.size(20.dp))
                            }
                            
                            if (goal == null) {
                                Text("Chưa đặt mục tiêu. Chạm để thiết lập.", color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f), modifier = Modifier.padding(top = 8.dp))
                            } else {
                                val currentAmount = if (goal!!.type == "REVENUE") totalRev else netIncome
                                val percent = if (goal!!.amount > 0) (currentAmount.toFloat() / goal!!.amount.toFloat()).coerceIn(0f, 1f) else 0f
                                val remain = goal!!.amount - currentAmount
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                                ) {
                                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(percent).background(MaterialTheme.colorScheme.primary))
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (remain > 0) "Còn thiếu ${FormatUtils.formatCurrency(remain)} để đạt mục tiêu" else "Đã đạt mục tiêu. Chúc mừng bạn!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    
                    if (showGoalDialog) {
                        GoalSettingDialog(
                            currentGoal = goal,
                            onDismiss = { showGoalDialog = false },
                            onSave = { type, amount ->
                                viewModel.saveGoal(type, amount)
                                showGoalDialog = false
                            },
                            onDelete = {
                                if (goal != null) { viewModel.deleteGoal(goal!!) }
                                showGoalDialog = false
                            }
                        )
                    }
                }
                
                // Summary Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Tổng thu nhập", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = FormatUtils.formatCurrency(netIncome),
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text("Doanh thu", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(FormatUtils.formatCurrency(totalRev), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Chi phí", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(FormatUtils.formatCurrency(totalExp), fontWeight = FontWeight.Bold, color = if (totalExp > 0) ExpenseError else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
                
                // Stats Card
                item {
                    Text("Thống kê", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            StatRow("Tổng số cuốc", "$totalTrips cuốc")
                            StatRow("Số ngày chạy", "$daysWorked ngày")
                            
                            val avgRevPerDay = if (daysWorked > 0) totalRev / daysWorked else 0L
                            StatRow("TB Doanh thu/ngày", FormatUtils.formatCurrency(avgRevPerDay))
                            
                            val avgNetPerDay = if (daysWorked > 0) netIncome / daysWorked else 0L
                            StatRow("TB Thu nhập/ngày", FormatUtils.formatCurrency(avgNetPerDay))
                            
                            val avgPerTrip = if (totalTrips > 0) totalRev / totalTrips else 0L
                            StatRow("TB Doanh thu/cuốc", FormatUtils.formatCurrency(avgPerTrip))

                            val totalKm = revenueEntries.sumOf { (it.distanceKm ?: 0f).toDouble() }.toFloat()
                            val totalHours = revenueEntries.sumOf { (it.durationHrs ?: 0f).toDouble() }.toFloat()

                            if (totalKm > 0) {
                                StatRow("Tổng số Km", "${String.format("%.1f", totalKm).replace(".", ",")} km")
                                val revPerKm = if (totalKm > 0) (totalRev / totalKm).toLong() else 0L
                                StatRow("Doanh thu/Km", FormatUtils.formatCurrency(revPerKm))
                            }

                            if (totalHours > 0) {
                                StatRow("Tổng giờ chạy", "${String.format("%.1f", totalHours).replace(".", ",")} giờ")
                                val revPerHour = if (totalHours > 0) (totalRev / totalHours).toLong() else 0L
                                StatRow("Doanh thu/giờ", FormatUtils.formatCurrency(revPerHour))
                            }
                        }
                    }
                }

                // Sources Breakdown
                item {
                    Text("Cơ cấu nguồn thu", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    val breakdownList = sources.map { source ->
                        val sourceRevenue = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.amount }
                        val sourceTrips = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.trips }
                        Triple(source, sourceRevenue, sourceTrips)
                    }.filter { it.second > 0 }.sortedByDescending { it.second }
                    
                    com.example.ui.components.RevenueBreakdown(
                        totalRevenue = totalRev,
                        breakdown = breakdownList
                    )
                }
                
                // Smart Stats
                item {
                    Text("Nhận xét thông minh", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Dựa trên dữ liệu hiện có:", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            val breakdownList = sources.map { source ->
                                val sourceRevenue = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.amount }
                                Pair(source, sourceRevenue)
                            }.filter { it.second > 0 }.sortedByDescending { it.second }
                            
                            if (breakdownList.isNotEmpty()) {
                                val topSource = breakdownList.first()
                                val topPercent = (topSource.second.toFloat() / totalRev.toFloat()) * 100
                                Text("• ${topSource.first.name} đang chiếm ${String.format("%.1f", topPercent).replace(".", ",")}% tổng doanh thu tháng này.", modifier = Modifier.padding(vertical = 4.dp))
                            }
                            
                            val dates = revenueEntries.groupBy { it.dateString }
                            if (dates.size >= 3) {
                                val topDate = dates.maxByOrNull { it.value.sumOf { r -> r.amount } }
                                if (topDate != null) {
                                    val topDateTimestamp = FormatUtils.parseDbDate(topDate.key)
                                    val dow = FormatUtils.getDayOfWeek(topDateTimestamp)
                                    Text("• Gần đây, $dow là ngày có doanh thu tốt nhất.", modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                            
                            val goalObj = viewModel.currentMonthGoal.collectAsState().value
                            if (goalObj != null && goalObj.amount > 0) {
                                val currentAmount = if (goalObj.type == "REVENUE") totalRev else netIncome
                                val remain = goalObj.amount - currentAmount
                                if (remain > 0) {
                                    // Calculate days left in month
                                    val cal = java.util.Calendar.getInstance()
                                    val maxDays = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                                    val currentDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
                                    val daysLeft = maxDays - currentDay
                                    if (daysLeft > 0) {
                                        val needPerDay = remain / daysLeft
                                        Text("• Còn $daysLeft ngày. Cần trung bình ${FormatUtils.formatCurrency(needPerDay)}/ngày để đạt mục tiêu ${FormatUtils.formatCurrency(goalObj.amount)}.", modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showMonthPicker) {
        com.example.ui.components.MonthYearPickerDialog(
            currentMonthStr = currentMonthStr,
            onDismiss = { showMonthPicker = false },
            onConfirm = { 
                viewModel.setMonth(it)
                showMonthPicker = false
            }
        )
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSettingDialog(
    currentGoal: com.example.data.Goal?,
    onDismiss: () -> Unit,
    onSave: (String, Long) -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var goalType by remember { mutableStateOf(currentGoal?.type ?: "REVENUE") }
        var amountStr by remember { mutableStateOf(if (currentGoal != null && currentGoal.amount > 0) currentGoal.amount.toString() else "") }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("THIẾT LẬP MỤC TIÊU", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tab Row for Goal Type
            TabRow(
                selectedTabIndex = if (goalType == "REVENUE") 0 else 1,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = goalType == "REVENUE",
                    onClick = { goalType = "REVENUE" },
                    text = { Text("Doanh thu") }
                )
                Tab(
                    selected = goalType == "NET_INCOME",
                    onClick = { goalType = "NET_INCOME" },
                    text = { Text("Thu nhập") }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) amountStr = newValue
                },
                label = { Text("Mục tiêu tháng (đ)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentGoal != null) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("XÓA", color = ExpenseError)
                    }
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("HỦY")
                }
                
                Button(
                    onClick = {
                        val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                        if (amount != null && amount > 0) {
                            onSave(goalType, amount)
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L
                ) {
                    Text("LƯU", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
