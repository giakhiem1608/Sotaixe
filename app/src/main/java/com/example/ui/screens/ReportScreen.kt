package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Goal
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils
import kotlin.math.max

@Composable
fun ReportScreen(viewModel: LedgerViewModel, onNavigateToMissingKm: () -> Unit) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val revenueEntries by viewModel.currentMonthRevenueEntries.collectAsState()
    val expenseEntries by viewModel.historyExpenseEntries.collectAsState()
    val sources by viewModel.allRevenueSources.collectAsState()
    val categories by viewModel.allExpenseCategories.collectAsState()
    val goal by viewModel.currentMonthGoal.collectAsState()

    var showGoalDialog by remember { mutableStateOf(false) }
    var showAdvancedStats by remember { mutableStateOf(false) }

    val totalRev = revenueEntries.sumOf { it.amount }
    val totalTip = revenueEntries.sumOf { it.tipAmount ?: 0L }
    val totalExp = expenseEntries.sumOf { it.amount }
    val netIncome = totalRev + totalTip - totalExp

    val totalTrips = revenueEntries.sumOf { it.trips }
    val daysWorked = revenueEntries.map { it.dateString }.distinct().size

    // KM logic
    val kmEntries = revenueEntries.filter { it.distanceKm != null }
    val totalKm = kmEntries.sumOf { (it.distanceKm ?: 0f).toDouble() }.toFloat()
    val revWithKm = kmEntries.sumOf { it.amount }
    
    val revPerKm = if (totalKm > 0f) (revWithKm / totalKm).toLong() else 0L
    
    val missingKmCount = revenueEntries.count { it.distanceKm == null }
    val kmCoverage = if (revenueEntries.isNotEmpty()) (kmEntries.size.toFloat() / revenueEntries.size * 100) else 0f

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

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. MỤC TIÊU
            GoalSection(goal = goal, netIncome = netIncome, totalRevenue = totalRev, currentMonth = currentMonth) { showGoalDialog = true }

            // 2. FINANCIAL SUMMARY
            FinancialSummaryCard(netIncome, totalRev, totalTip, totalExp)

            // 3. KPI NHANH
            QuickKpiSection(daysWorked, totalTrips, totalKm, netIncome, totalRev, revPerKm, showAdvancedStats) {
                showAdvancedStats = !showAdvancedStats
            }

            // 4. CƠ CẤU NGUỒN THU
            if (revenueEntries.isNotEmpty()) {
                RevenueStructureSection(revenueEntries, sources, totalRev)
            }

            // 5. CHI PHÍ
            if (expenseEntries.isNotEmpty()) {
                ExpenseStructureSection(expenseEntries, categories, totalExp)
            }

            // 6. TIP
            if (totalTip > 0) {
                TipReportSection(revenueEntries, totalTip)
            }

            // 7. DATA QUALITY
            DataQualitySection(kmEntries.size, revenueEntries.size, kmCoverage, missingKmCount, onNavigateToMissingKm)
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

@Composable
fun GoalSection(goal: Goal?, netIncome: Long, totalRevenue: Long, currentMonth: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (goal?.type == "REVENUE") "MỤC TIÊU DOANH THU THÁNG ${FormatUtils.formatDisplayMonth(currentMonth).substring(6)}" else "MỤC TIÊU THU NHẬP THÁNG ${FormatUtils.formatDisplayMonth(currentMonth).substring(6)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(Icons.Filled.Edit, contentDescription = "Sửa", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            if (goal == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Chưa đặt mục tiêu", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                val current = if (goal.type == "REVENUE") totalRevenue else netIncome
                val pct = if (goal.amount > 0) (current.toDouble() / goal.amount * 100) else 0.0
                val remaining = max(goal.amount - current, 0L)
                val isReached = current >= goal.amount
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("${FormatUtils.formatCurrency(current)} / ${FormatUtils.formatCurrency(goal.amount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    Text("${String.format("%.1f", pct)}%", fontWeight = FontWeight.Bold, color = if (isReached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = (pct.toFloat() / 100f).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (isReached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                if (isReached) {
                    val over = current - goal.amount
                    Text("✓ Đã đạt mục tiêu (vượt ${FormatUtils.formatCurrency(over)})", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                } else {
                    Text("Còn ${FormatUtils.formatCurrency(remaining)} để đạt mục tiêu", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun FinancialSummaryCard(netIncome: Long, rev: Long, tip: Long, exp: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("TỔNG THU NHẬP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(FormatUtils.formatCurrency(netIncome), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Doanh thu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(FormatUtils.formatCurrency(rev), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                if (tip > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tip", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("+${FormatUtils.formatCurrency(tip)}", fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Chi phí", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("-${FormatUtils.formatCurrency(exp)}", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                }
            }
        }
    }
}

@Composable
fun QuickKpiSection(daysWorked: Int, totalTrips: Int, totalKm: Float, netIncome: Long, rev: Long, revPerKm: Long, showAdvanced: Boolean, onToggle: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("KPI NHANH", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiBox("Số ngày chạy", "$daysWorked", modifier = Modifier.weight(1f))
            KpiBox("Tổng cuốc", "$totalTrips", modifier = Modifier.weight(1f))
            KpiBox("Tổng KM", "${String.format("%.1f", totalKm).replace(".", ",")} km", modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        val avgNetPerDay = if (daysWorked > 0) netIncome / daysWorked else 0L
        val avgRevPerTrip = if (totalTrips > 0) rev / totalTrips else 0L
        
        StatRow("TB Thu nhập/ngày", FormatUtils.formatCurrency(avgNetPerDay))
        StatRow("TB Doanh thu/cuốc", FormatUtils.formatCurrency(avgRevPerTrip))
        StatRow("Doanh thu/KM", FormatUtils.formatCurrency(revPerKm))
        
        if (showAdvanced) {
            val netPerKm = if (rev > 0) (revPerKm * (netIncome.toDouble() / rev)).toLong() else 0L
            StatRow("Thu nhập/KM", FormatUtils.formatCurrency(netPerKm))
        }
        
        TextButton(onClick = onToggle, modifier = Modifier.fillMaxWidth()) {
            Text(if (showAdvanced) "Ẩn bớt chỉ số" else "Xem thêm chỉ số")
        }
    }
}

@Composable
fun KpiBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
    Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
}

@Composable
fun RevenueStructureSection(entries: List<com.example.data.RevenueEntry>, sources: List<com.example.data.RevenueSource>, totalRev: Long) {
    Column {
        Text("CƠ CẤU DOANH THU", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        
        val grouped = entries.groupBy { it.sourceId }
        val sortedGroups = grouped.mapValues { it.value.sumOf { r -> r.amount } }.toList().sortedByDescending { it.second }
        
        Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {
            sortedGroups.forEach { (sourceId, amount) ->
                val weight = amount.toFloat() / totalRev.toFloat()
                if (weight > 0f) {
                    val source = sources.find { it.id == sourceId }
                    val color = try { Color(android.graphics.Color.parseColor(source?.colorHex ?: "#CCCCCC")) } catch(e: Exception) { Color.Gray }
                    Box(modifier = Modifier.weight(weight).fillMaxHeight().background(color))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            sortedGroups.forEach { (sourceId, amount) ->
                val source = sources.find { it.id == sourceId }
                val color = try { Color(android.graphics.Color.parseColor(source?.colorHex ?: "#CCCCCC")) } catch(e: Exception) { Color.Gray }
                val tripsCount = grouped[sourceId]?.sumOf { it.trips } ?: 0
                val pct = if (totalRev > 0) (amount.toDouble() / totalRev * 100) else 0.0
                
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(source?.name ?: "Khác", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text("$tripsCount cuốc • ${String.format("%.1f", pct)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(FormatUtils.formatCurrency(amount), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun ExpenseStructureSection(entries: List<com.example.data.ExpenseEntry>, categories: List<com.example.data.ExpenseCategory>, totalExp: Long) {
    Column {
        Text("CƠ CẤU CHI PHÍ", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        
        val grouped = entries.groupBy { it.categoryId }
        val sortedGroups = grouped.mapValues { it.value.sumOf { r -> r.amount } }.toList().sortedByDescending { it.second }
        
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            sortedGroups.forEach { (catId, amount) ->
                val cat = categories.find { it.id == catId }
                val pct = if (totalExp > 0) (amount.toDouble() / totalExp * 100) else 0.0
                
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cat?.name ?: "Khác", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text("${String.format("%.1f", pct)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(FormatUtils.formatCurrency(amount), fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun TipReportSection(entries: List<com.example.data.RevenueEntry>, totalTip: Long) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("TIP", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        
        val tipEntries = entries.filter { (it.tipAmount ?: 0L) > 0L }
        val tipTripsCount = tipEntries.sumOf { it.trips } // Not exactly accurate if 1 entry has 2 trips but tip is for 1, but we sum trips of entries that have tip
        val avgTip = if (tipTripsCount > 0) totalTip / tipTripsCount else 0L
        
        StatRow("Tổng Tip", FormatUtils.formatCurrency(totalTip))
        StatRow("Cuốc có Tip", "$tipTripsCount cuốc")
        StatRow("TB Tip/cuốc có Tip", FormatUtils.formatCurrency(avgTip))
    }
}

@Composable
fun DataQualitySection(kmEntriesCount: Int, totalEntriesCount: Int, kmCoverage: Float, missingKmCount: Int, onNavigateToMissingKm: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("CHẤT LƯỢNG DỮ LIỆU", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Dữ liệu KM:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("$kmEntriesCount/$totalEntriesCount cuốc đã cập nhật", fontWeight = FontWeight.Bold)
                    Text("${String.format("%.1f", kmCoverage)}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                
                if (missingKmCount > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToMissingKm() }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("⚠ $missingKmCount cuốc chưa có KM", color = Color(0xFFD97706), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text("Cập nhật ngay để tính KPI chính xác", color = Color(0xFFD97706), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("✓ KM đã đầy đủ", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun GoalSettingDialog(
    currentGoal: com.example.data.Goal?,
    onDismiss: () -> Unit,
    onSave: (String, Long) -> Unit,
    onDelete: () -> Unit
) {
    var goalType by remember { mutableStateOf(currentGoal?.type ?: "REVENUE") }
    var amountStr by remember { mutableStateOf(if (currentGoal != null && currentGoal.amount > 0) currentGoal.amount.toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (currentGoal == null) "Thêm mục tiêu" else "Cập nhật mục tiêu") },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = goalType == "REVENUE", onClick = { goalType = "REVENUE" }, label = { Text("Doanh thu") })
                    FilterChip(selected = goalType == "NET_INCOME", onClick = { goalType = "NET_INCOME" }, label = { Text("Thu nhập") })
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
                    label = { Text("Mục tiêu tháng (đ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L
                if (amount > 0) onSave(goalType, amount)
            }) { Text("LƯU") }
        },
        dismissButton = {
            Row {
                if (currentGoal != null) {
                    TextButton(onClick = onDelete) { Text("XÓA", color = MaterialTheme.colorScheme.error) }
                }
                TextButton(onClick = onDismiss) { Text("HỦY") }
            }
        }
    )
}
