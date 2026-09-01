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
import com.example.ui.theme.ColorExpense
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
            Text(
                text = "BÁO CÁO THÁNG $displayMonth",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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
                                Text("MỤC TIÊU THÁNG $displayMonth", fontWeight = FontWeight.Bold)
                                Icon(Icons.Filled.Edit, contentDescription = "Sửa mục tiêu", modifier = Modifier.size(20.dp))
                            }
                            
                            if (goal == null) {
                                Text("Chưa đặt mục tiêu. Chạm để thiết lập.", color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f), modifier = Modifier.padding(top = 8.dp))
                            } else {
                                val currentAmount = if (goal!!.type == "REVENUE") totalRev else netIncome
                                val percent = if (goal!!.amount > 0) (currentAmount.toFloat() / goal!!.amount.toFloat()).coerceIn(0f, 1f) else 0f
                                val remain = goal!!.amount - currentAmount
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${FormatUtils.formatCurrency(currentAmount)} / ${FormatUtils.formatCurrency(goal!!.amount)}", fontWeight = FontWeight.Bold)
                                    Text("${(percent * 100).toInt()}%")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { percent },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (remain > 0) {
                                    Text("Còn ${FormatUtils.formatCurrency(remain)}", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    Text("Đã đạt mục tiêu! Tuyệt vời!", style = MaterialTheme.typography.bodySmall, color = ColorGrab)
                                }
                            }
                        }
                    }

                    if (showGoalDialog) {
                        var goalAmountStr by remember { mutableStateOf(goal?.amount?.toString() ?: "") }
                        var goalType by remember { mutableStateOf(goal?.type ?: "NET_INCOME") }

                        AlertDialog(
                            onDismissRequest = { showGoalDialog = false },
                            title = { Text("Thiết lập mục tiêu") },
                            text = {
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                        FilterChip(selected = goalType == "REVENUE", onClick = { goalType = "REVENUE" }, label = { Text("Doanh thu") })
                                        FilterChip(selected = goalType == "NET_INCOME", onClick = { goalType = "NET_INCOME" }, label = { Text("Thu nhập") })
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = goalAmountStr,
                                        onValueChange = { if (it.all { char -> char.isDigit() }) goalAmountStr = it },
                                        label = { Text("Số tiền mục tiêu (đ)") },
                                        visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                                        singleLine = true
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    val amount = goalAmountStr.toLongOrNull()
                                    if (amount != null) {
                                        viewModel.saveGoal(goalType, amount)
                                        showGoalDialog = false
                                    }
                                }) {
                                    Text("LƯU")
                                }
                            },
                            dismissButton = {
                                Row {
                                    if (goal != null) {
                                        TextButton(onClick = {
                                            viewModel.deleteGoal(goal!!)
                                            showGoalDialog = false
                                        }) { Text("XÓA", color = MaterialTheme.colorScheme.error) }
                                    }
                                    TextButton(onClick = { showGoalDialog = false }) { Text("HỦY") }
                                }
                            }
                        )
                    }
                }

                // Overview Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Tổng Thu Nhập", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                            Text(
                                FormatUtils.formatCurrency(netIncome),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Doanh thu", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                                    Text(FormatUtils.formatCurrency(totalRev), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Chi phí", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                                    Text(FormatUtils.formatCurrency(totalExp), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Stats Details
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Thống Kê", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                            
                            StatRow("Số ngày chạy", "$daysWorked ngày")
                            StatRow("Tổng số cuốc", "$totalTrips cuốc")
                            
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
                    Text("Cơ Cấu Nguồn Thu", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    
                    val breakdown = sources.map { source ->
                        val sourceRevenue = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.amount }
                        val sourceTrips = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.trips }
                        Triple(source, sourceRevenue, sourceTrips)
                    }.filter { it.second > 0 }.sortedByDescending { it.second }

                    breakdown.forEach { (source, amount, trips) ->
                        val percent = if (totalRev > 0) (amount.toFloat() / totalRev.toFloat()) * 100 else 0f
                        com.example.ui.screens.RevenueBreakdownItem(source, amount, trips, percent)
                    }
                }
                
                // Smart Stats
                item {
                    Text("Nhận Xét Thông Minh", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (daysWorked < 3) {
                                Text("• Dựa trên dữ liệu ít ỏi hiện có:", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            val breakdown = sources.map { source ->
                                val sourceRevenue = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.amount }
                                Pair(source, sourceRevenue)
                            }.filter { it.second > 0 }.sortedByDescending { it.second }
                            
                            if (breakdown.isNotEmpty()) {
                                val topSource = breakdown.first()
                                val topPercent = (topSource.second.toFloat() / totalRev.toFloat()) * 100
                                Text("• ${topSource.first.name} đang chiếm ${String.format("%.1f", topPercent).replace(".", ",")}% tổng doanh thu tháng này.", modifier = Modifier.padding(vertical = 4.dp))

                                if (breakdown.size >= 2) {
                                    val secondSource = breakdown[1]
                                    val firstTrips = revenueEntries.filter { it.sourceId == topSource.first.id }.sumOf { it.trips }
                                    val secondTrips = revenueEntries.filter { it.sourceId == secondSource.first.id }.sumOf { it.trips }
                                    val firstAvgTrip = if (firstTrips > 0) topSource.second / firstTrips else 0L
                                    val secondAvgTrip = if (secondTrips > 0) secondSource.second / secondTrips else 0L
                                    if (firstAvgTrip > 0 && secondAvgTrip > 0) {
                                        val diffPercent = ((Math.abs(firstAvgTrip - secondAvgTrip).toFloat() / Math.min(firstAvgTrip, secondAvgTrip).toFloat()) * 100).toInt()
                                        if (diffPercent > 0) {
                                            val higher = if (firstAvgTrip > secondAvgTrip) topSource.first.name else secondSource.first.name
                                            val lower = if (firstAvgTrip > secondAvgTrip) secondSource.first.name else topSource.first.name
                                            Text("• Doanh thu trung bình/cuốc của $higher cao hơn $lower khoảng $diffPercent%.", modifier = Modifier.padding(vertical = 4.dp))
                                        }
                                    }
                                }
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
