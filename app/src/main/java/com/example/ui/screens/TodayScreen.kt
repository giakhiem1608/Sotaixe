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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseCategory
import com.example.data.RevenueSource
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

val BgColor = Color(0xFFF8FAFC) // Slate 50
val CardSurface = Color(0xFFFFFFFF)
val CardBorder = Color(0xFFE2E8F0)

@Composable
fun TodayScreen(viewModel: LedgerViewModel, onNavigateToMissingKm: () -> Unit) {
    val currentDate by viewModel.currentDate.collectAsState()
    val todaysRevenueEntries by viewModel.todaysRevenueEntries.collectAsState()
    val todaysTotalRevenue by viewModel.todaysTotalRevenue.collectAsState()
    val todaysTotalTip by viewModel.todaysTotalTip.collectAsState()
    val todaysTotalExpense by viewModel.todaysTotalExpense.collectAsState()
    val todaysNetIncome by viewModel.todaysNetIncome.collectAsState()
    val todaysTotalTrips by viewModel.todaysTotalTrips.collectAsState()
    val todaysTotalDistance by viewModel.todaysTotalDistance.collectAsState()
    
    val sources by viewModel.allRevenueSources.collectAsState()
    val categories by viewModel.allExpenseCategories.collectAsState()
    
    val primaryHex by viewModel.primaryColorHex.collectAsState()
    val heroBgHex by viewModel.heroBgColorHex.collectAsState()

    var showAddRevenue by remember { mutableStateOf(false) }
    var showAddExpense by remember { mutableStateOf(false) }

    val missingKmCount = todaysRevenueEntries.count { it.distanceKm == null }

    val primaryColor = try { Color(android.graphics.Color.parseColor(primaryHex)) } catch(e: Exception) { Color(0xFF0284C7) }

    Column(modifier = Modifier.fillMaxSize().background(BgColor)) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousDay() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF475569)) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(FormatUtils.formatDisplayDate(currentDate), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                val calendar = java.util.Calendar.getInstance().apply { timeInMillis = currentDate }
                val dayOfWeek = when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
                    java.util.Calendar.SUNDAY -> "CHỦ NHẬT"
                    java.util.Calendar.MONDAY -> "THỨ HAI"
                    java.util.Calendar.TUESDAY -> "THỨ BA"
                    java.util.Calendar.WEDNESDAY -> "THỨ TƯ"
                    java.util.Calendar.THURSDAY -> "THỨ NĂM"
                    java.util.Calendar.FRIDAY -> "THỨ SÁU"
                    java.util.Calendar.SATURDAY -> "THỨ BẢY"
                    else -> ""
                }
                Text(dayOfWeek, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
            }
            IconButton(onClick = { viewModel.nextDay() }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF475569)) }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            
            // HERO CARD
            HeroIncomeCard(
                netIncome = todaysNetIncome,
                revenue = todaysTotalRevenue,
                tip = todaysTotalTip,
                expense = todaysTotalExpense,
                trips = todaysTotalTrips,
                distance = todaysTotalDistance,
                bgHex = heroBgHex
            )
            
            // QUICK ACTIONS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { showAddRevenue = true },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp)
                ) {
                    Text("+ Doanh thu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Surface(
                    onClick = { showAddExpense = true },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder),
                    shadowElevation = 1.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("− Chi phí", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF334155))
                    }
                }
            }
            
            // MISSING KM NOTICE
            if (missingKmCount > 0) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToMissingKm() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("⚠ $missingKmCount cuốc chưa có KM", color = Color(0xFFB45309), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Text("Cập nhật >", color = Color(0xFFD97706), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // REVENUE STRUCTURE
            if (todaysRevenueEntries.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("CƠ CẤU DOANH THU", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        val grouped = todaysRevenueEntries.groupBy { it.sourceId }
                        val sortedGroups = grouped.mapValues { it.value.sumOf { r -> r.amount } }.toList().sortedByDescending { it.second }
                        
                        // Bar
                        Row(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))) {
                            sortedGroups.forEach { (sourceId, amount) ->
                                val weight = amount.toFloat() / todaysTotalRevenue.toFloat()
                                if (weight > 0f) {
                                    val source = sources.find { it.id == sourceId }
                                    val color = try { Color(android.graphics.Color.parseColor(source?.colorHex ?: "#94A3B8")) } catch(e: Exception) { Color(0xFF94A3B8) }
                                    Box(modifier = Modifier.weight(weight).fillMaxHeight().background(color))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // List
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            sortedGroups.forEach { (sourceId, amount) ->
                                val source = sources.find { it.id == sourceId }
                                val color = try { Color(android.graphics.Color.parseColor(source?.colorHex ?: "#94A3B8")) } catch(e: Exception) { Color(0xFF94A3B8) }
                                val tripsCount = grouped[sourceId]?.sumOf { it.trips } ?: 0
                                val pct = if (todaysTotalRevenue > 0) (amount.toDouble() / todaysTotalRevenue * 100) else 0.0
                                
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(source?.name ?: "Khác", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1E293B))
                                        Text("$tripsCount cuốc • ${String.format("%.1f", pct)}%", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                    }
                                    Text(FormatUtils.formatCurrency(amount), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF0F172A))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddRevenue) {
        AddRevenueSheet(
            sources = sources,
            primaryColor = primaryColor,
            onDismiss = { showAddRevenue = false },
            onSave = { srcId, amt, tip, trips, dist, note ->
                viewModel.addRevenue(srcId, amt, tip, trips, null, dist, note)
                showAddRevenue = false
            }
        )
    }

    if (showAddExpense) {
        AddExpenseSheet(
            categories = categories,
            primaryColor = primaryColor,
            onDismiss = { showAddExpense = false },
            onSave = { catId, amt, note ->
                viewModel.addExpense(catId, amt, note)
                showAddExpense = false
            }
        )
    }
}

@Composable
fun HeroIncomeCard(
    netIncome: Long,
    revenue: Long,
    tip: Long,
    expense: Long,
    trips: Int,
    distance: Float,
    bgHex: String
) {
    val bgColor = try { Color(android.graphics.Color.parseColor(bgHex)) } catch(e: Exception) { Color(0xFF0369A1) }
    val isLight = bgColor.luminance() > 0.5f
    val defaultText = if (isLight) Color(0xFF0F172A) else Color.White
    val secondaryText = if (isLight) Color(0xFF475569) else Color.White.copy(alpha = 0.7f)
    
    val tipColor = if (isLight) Color(0xFF059669) else Color(0xFF34D399) // Emerald
    val expColor = if (isLight) Color(0xFFDC2626) else Color(0xFFF87171) // Red

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, focusedElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("THU NHẬP HÔM NAY", style = MaterialTheme.typography.labelMedium, color = secondaryText, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(FormatUtils.formatCurrency(netIncome), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = defaultText)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Doanh thu", style = MaterialTheme.typography.labelSmall, color = secondaryText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(FormatUtils.formatCurrency(revenue), fontWeight = FontWeight.SemiBold, color = defaultText, style = MaterialTheme.typography.bodyLarge)
                }
                if (tip > 0) {
                    Column {
                        Text("Tip", style = MaterialTheme.typography.labelSmall, color = secondaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("+${FormatUtils.formatCurrency(tip)}", fontWeight = FontWeight.Bold, color = tipColor, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Chi phí", style = MaterialTheme.typography.labelSmall, color = secondaryText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("-${FormatUtils.formatCurrency(expense)}", fontWeight = FontWeight.Bold, color = expColor, style = MaterialTheme.typography.bodyLarge)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = secondaryText.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            
            val avgPerTrip = if (trips > 0) revenue / trips else 0L
            Text(
                "$trips cuốc  •  ${String.format("%.1f", distance).replace(".", ",")} km  •  TB ${FormatUtils.formatCurrency(avgPerTrip)}/cuốc",
                style = MaterialTheme.typography.bodySmall,
                color = secondaryText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRevenueSheet(
    sources: List<RevenueSource>,
    primaryColor: Color,
    onDismiss: () -> Unit,
    onSave: (Int, Long, Long?, Int, Float?, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        var selectedSourceId by remember { mutableStateOf(sources.firstOrNull()?.id ?: 0) }
        var amountStr by remember { mutableStateOf("") }
        var tipStr by remember { mutableStateOf("") }
        var tripsStr by remember { mutableStateOf("1") }
        var distanceStr by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 32.dp).imePadding().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Thêm doanh thu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            
            // Segmented Selector Equivalent
            Row(modifier = Modifier.fillMaxWidth().height(48.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)).padding(4.dp)) {
                sources.forEach { source ->
                    val isSelected = selectedSourceId == source.id
                    val sourceColor = try { Color(android.graphics.Color.parseColor(source.colorHex)) } catch (e: Exception) { Color.Gray }
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CardSurface else Color.Transparent)
                            .clickable { selectedSourceId = source.id },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(sourceColor))
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                source.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFF64748B),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
                label = { Text("Số tiền thực nhận (đ) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = tripsStr,
                    onValueChange = { tripsStr = it },
                    label = { Text("Số cuốc *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = distanceStr,
                    onValueChange = { distanceStr = it.replace(",", ".") },
                    label = { Text("Số KM (Tùy chọn)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }
            
            OutlinedTextField(
                value = tipStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) tipStr = newValue },
                label = { Text("Tiền tip (Tùy chọn)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    val tip = tipStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    val trips = tripsStr.toIntOrNull()
                    val dist = distanceStr.toFloatOrNull()
                    if (amount != null && trips != null && trips >= 1 && selectedSourceId != 0) {
                        onSave(selectedSourceId, amount, tip, trips, dist, note)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L && (tripsStr.toIntOrNull() ?: 0) >= 1
            ) {
                Text("LƯU DOANH THU", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseSheet(
    categories: List<ExpenseCategory>,
    primaryColor: Color,
    onDismiss: () -> Unit,
    onSave: (Int, Long, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: 0) }
        var amountStr by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 32.dp).imePadding().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Thêm chi phí", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategoryId == category.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor.copy(alpha = 0.1f),
                            selectedLabelColor = primaryColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) primaryColor else CardBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
                label = { Text("Số tiền (đ) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    if (amount != null && selectedCategoryId != 0) {
                        onSave(selectedCategoryId, amount, note)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L
            ) {
                Text("LƯU CHI PHÍ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
