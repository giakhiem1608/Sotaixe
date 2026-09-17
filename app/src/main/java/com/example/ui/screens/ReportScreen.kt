package com.example.ui.screens


import androidx.compose.foundation.BorderStroke
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
    
    val primaryHex by viewModel.primaryColorHex.collectAsState()
    val primaryColor = try { Color(android.graphics.Color.parseColor(primaryHex)) } catch(e: Exception) { Color(0xFF0284C7) }

    var showGoalDialog by remember { mutableStateOf(false) }
    var showAdvancedStats by remember { mutableStateOf(false) }
    
    val monthExpEntries = expenseEntries.filter { it.dateString.startsWith(currentMonth) }

    val totalRev = revenueEntries.sumOf { it.amount }
    val totalTip = revenueEntries.sumOf { it.tipAmount ?: 0L }
    val totalExp = monthExpEntries.sumOf { it.amount }
    val netIncome = totalRev + totalTip - totalExp

    val totalTrips = revenueEntries.sumOf { it.trips }
    val daysWorked = revenueEntries.map { it.dateString }.distinct().size

    val kmEntries = revenueEntries.filter { it.distanceKm != null }
    val totalKm = kmEntries.sumOf { (it.distanceKm ?: 0f).toDouble() }.toFloat()
    val revWithKm = kmEntries.sumOf { it.amount }
    
    val revPerKm = if (totalKm > 0f) (revWithKm / totalKm).toLong() else 0L
    
    val missingKmCount = revenueEntries.count { it.distanceKm == null }
    val kmCoverage = if (revenueEntries.isNotEmpty()) (kmEntries.size.toFloat() / revenueEntries.size * 100) else 0f

    val displayMonth = FormatUtils.formatDisplayMonth(currentMonth)

    Column(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousMonth() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF475569)) }
            Text(displayMonth, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            IconButton(onClick = { viewModel.nextMonth() }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF475569)) }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            
            // 1. MỤC TIÊU
            GoalSection(goal = goal, netIncome = netIncome, totalRevenue = totalRev, displayMonth = displayMonth, primaryColor = primaryColor) { showGoalDialog = true }

            // 2. FINANCIAL SUMMARY (MONTHLY INCOME HERO)
            FinancialSummarySection(netIncome, totalRev, totalTip, totalExp)

            HorizontalDivider(color = CardBorder, thickness = 1.dp)

            // 3. KPI QUICK
            OverviewSection(daysWorked, totalTrips, totalKm)

            // 4. HIỆU QUẢ
            EfficiencySection(daysWorked, totalTrips, netIncome, totalRev, revPerKm, showAdvancedStats, primaryColor) {
                showAdvancedStats = !showAdvancedStats
            }

            // 5. CƠ CẤU DOANH THU
            if (revenueEntries.isNotEmpty()) {
                RevenueStructureSection(revenueEntries, sources, totalRev)
            }

            // 6. CƠ CẤU CHI PHÍ
            if (monthExpEntries.isNotEmpty()) {
                ExpenseStructureSection(monthExpEntries, categories, totalExp)
            }

            // 8. CHẤT LƯỢNG DỮ LIỆU
            if (missingKmCount > 0) {
                DataQualitySection(missingKmCount, onNavigateToMissingKm)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showGoalDialog) {
        GoalSettingDialog(
            currentGoal = goal,
            primaryColor = primaryColor,
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
fun GoalSection(goal: Goal?, netIncome: Long, totalRevenue: Long, displayMonth: String, primaryColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MỤC TIÊU THÁNG $displayMonth",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Icon(Icons.Filled.Edit, contentDescription = "Sửa", modifier = Modifier.size(16.dp), tint = Color(0xFF94A3B8))
            }
            
            if (goal == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Chưa đặt mục tiêu", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodyMedium)
            } else {
                val current = if (goal.type == "REVENUE") totalRevenue else netIncome
                val pct = if (goal.amount > 0) (current.toDouble() / goal.amount * 100) else 0.0
                val remaining = max(goal.amount - current, 0L)
                val isReached = current >= goal.amount
                
                val labelType = if (goal.type == "REVENUE") "Doanh thu" else "Thu nhập"
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text(labelType, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(FormatUtils.formatCurrency(current), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = Color(0xFF0F172A))
                            Text(" / ${FormatUtils.formatCurrency(goal.amount)}", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B), modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }
                    Text("${String.format("%.1f", pct)}%", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = if (isReached) Color(0xFF059669) else primaryColor)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = (pct.toFloat() / 100f).coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (isReached) Color(0xFF059669) else primaryColor,
                    trackColor = Color(0xFFF1F5F9)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                if (isReached) {
                    val over = current - goal.amount
                    Text("✓ Đã đạt mục tiêu (vượt ${FormatUtils.formatCurrency(over)})", color = Color(0xFF059669), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Còn ${FormatUtils.formatCurrency(remaining)} để đạt mục tiêu", color = Color(0xFF64748B), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun FinancialSummarySection(netIncome: Long, rev: Long, tip: Long, exp: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text("TỔNG THU NHẬP", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(FormatUtils.formatCurrency(netIncome), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Doanh thu", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF475569))
                Text(FormatUtils.formatCurrency(rev), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF0F172A))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tip", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF475569))
                Text(if (tip > 0) "+${FormatUtils.formatCurrency(tip)}" else FormatUtils.formatCurrency(0), fontWeight = FontWeight.Bold, color = Color(0xFF059669), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Chi phí", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF475569))
                Text("-${FormatUtils.formatCurrency(exp)}", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun OverviewSection(daysWorked: Int, totalTrips: Int, totalKm: Float) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OverviewCard(modifier = Modifier.weight(1f), label = "Ngày chạy", value = "$daysWorked")
        OverviewCard(modifier = Modifier.weight(1f), label = "Số cuốc", value = "$totalTrips")
        OverviewCard(modifier = Modifier.weight(1f), label = "Tổng KM", value = String.format("%.1f", totalKm).replace(".", ","))
    }
}

@Composable
fun OverviewCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        }
    }
}

@Composable
fun EfficiencySection(daysWorked: Int, totalTrips: Int, netIncome: Long, rev: Long, revPerKm: Long, showAdvanced: Boolean, primaryColor: Color, onToggle: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Hiệu quả", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(16.dp))
        
        val avgNetPerDay = if (daysWorked > 0) netIncome / daysWorked else 0L
        val avgRevPerTrip = if (totalTrips > 0) rev / totalTrips else 0L
        
        StatRow("TB Thu nhập/ngày", FormatUtils.formatCurrency(avgNetPerDay))
        StatRow("TB Doanh thu/cuốc", FormatUtils.formatCurrency(avgRevPerTrip))
        StatRow("Doanh thu/KM", FormatUtils.formatCurrency(revPerKm))
        
        if (showAdvanced) {
            val netPerKm = if (rev > 0) (revPerKm * (netIncome.toDouble() / rev)).toLong() else 0L
            StatRow("Thu nhập/KM", FormatUtils.formatCurrency(netPerKm))
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (showAdvanced) "Ẩn bớt chỉ số" else "Xem thêm chỉ số",
            color = primaryColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onToggle() }.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF475569))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
    }
    HorizontalDivider(color = CardBorder, thickness = 1.dp)
}

@Composable
fun RevenueStructureSection(entries: List<com.example.data.RevenueEntry>, sources: List<com.example.data.RevenueSource>, totalRev: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("CƠ CẤU DOANH THU", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(20.dp))
            
            val grouped = entries.groupBy { it.sourceId }
            val sortedGroups = grouped.mapValues { it.value.sumOf { r -> r.amount } }.toList().sortedByDescending { it.second }
            
            Row(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))) {
                sortedGroups.forEach { (sourceId, amount) ->
                    val weight = amount.toFloat() / totalRev.toFloat()
                    if (weight > 0f) {
                        val source = sources.find { it.id == sourceId }
                        val color = try { Color(android.graphics.Color.parseColor(source?.colorHex ?: "#94A3B8")) } catch(e: Exception) { Color(0xFF94A3B8) }
                        Box(modifier = Modifier.weight(weight).fillMaxHeight().background(color))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                sortedGroups.forEach { (sourceId, amount) ->
                    val source = sources.find { it.id == sourceId }
                    val color = try { Color(android.graphics.Color.parseColor(source?.colorHex ?: "#94A3B8")) } catch(e: Exception) { Color(0xFF94A3B8) }
                    val tripsCount = grouped[sourceId]?.sumOf { it.trips } ?: 0
                    val pct = if (totalRev > 0) (amount.toDouble() / totalRev * 100) else 0.0
                    
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(source?.name ?: "Khác", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF0F172A))
                            Text("$tripsCount cuốc • ${String.format("%.1f", pct)}%", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        }
                        Text(FormatUtils.formatCurrency(amount), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF0F172A))
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseStructureSection(entries: List<com.example.data.ExpenseEntry>, categories: List<com.example.data.ExpenseCategory>, totalExp: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("CƠ CẤU CHI PHÍ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(20.dp))
            
            val grouped = entries.groupBy { it.categoryId }
            val sortedGroups = grouped.mapValues { it.value.sumOf { r -> r.amount } }.toList().sortedByDescending { it.second }
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                sortedGroups.forEach { (catId, amount) ->
                    val cat = categories.find { it.id == catId }
                    val pct = if (totalExp > 0) (amount.toDouble() / totalExp * 100) else 0.0
                    
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cat?.name ?: "Khác", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF0F172A))
                            Text("${String.format("%.1f", pct)}%", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        }
                        Text(FormatUtils.formatCurrency(amount), fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun DataQualitySection(missingKmCount: Int, onNavigateToMissingKm: () -> Unit) {
    Surface(
        color = Color(0xFFFEF3C7),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
        modifier = Modifier.fillMaxWidth().clickable { onNavigateToMissingKm() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("⚠ $missingKmCount cuốc chưa có KM", color = Color(0xFFB45309), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Cập nhật ngay để tính KPI chính xác", color = Color(0xFFD97706), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun GoalSettingDialog(
    currentGoal: Goal?,
    primaryColor: Color,
    onDismiss: () -> Unit,
    onSave: (String, Long) -> Unit,
    onDelete: () -> Unit
) {
    var goalType by remember { mutableStateOf(currentGoal?.type ?: "REVENUE") }
    var amountStr by remember { mutableStateOf(if (currentGoal != null && currentGoal.amount > 0) currentGoal.amount.toString() else "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        shape = RoundedCornerShape(24.dp),
        title = { Text(if (currentGoal == null) "Thêm mục tiêu" else "Cập nhật mục tiêu", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val revSel = goalType == "REVENUE"
                    FilterChip(
                        selected = revSel, 
                        onClick = { goalType = "REVENUE" }, 
                        label = { Text("Doanh thu") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryColor.copy(alpha=0.1f), selectedLabelColor = primaryColor)
                    )
                    val netSel = goalType == "NET_INCOME"
                    FilterChip(
                        selected = netSel, 
                        onClick = { goalType = "NET_INCOME" }, 
                        label = { Text("Thu nhập") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryColor.copy(alpha=0.1f), selectedLabelColor = primaryColor)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
                    label = { Text("Mục tiêu tháng (đ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L
                if (amount > 0) onSave(goalType, amount)
            }, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) { Text("LƯU", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            Row {
                if (currentGoal != null) {
                    TextButton(onClick = onDelete) { Text("XÓA", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold) }
                }
                TextButton(onClick = onDismiss) { Text("HỦY", color = Color(0xFF64748B), fontWeight = FontWeight.Bold) }
            }
        }
    )
}
