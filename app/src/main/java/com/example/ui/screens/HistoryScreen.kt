package com.example.ui.screens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseEntry
import com.example.data.RevenueEntry
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils


@Composable
fun HistoryScreen(viewModel: LedgerViewModel) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val revenueEntries by viewModel.currentMonthRevenueEntries.collectAsState()
    val allExpenseEntries by viewModel.historyExpenseEntries.collectAsState()
    val expenseEntries = allExpenseEntries.filter { it.dateString.startsWith(currentMonth) }
    
    val sources by viewModel.allRevenueSources.collectAsState()
    val categories by viewModel.allExpenseCategories.collectAsState()
    val missingKmActive by viewModel.missingKmFilterActive.collectAsState()
    
    val primaryHex by viewModel.primaryColorHex.collectAsState()
    val primaryColor = try { Color(android.graphics.Color.parseColor(primaryHex)) } catch(e: Exception) { Color(0xFF0284C7) }

    var selectedFilter by remember { mutableStateOf(if (missingKmActive) "missing_km" else "all") }
    
    LaunchedEffect(missingKmActive) {
        if (missingKmActive) {
            selectedFilter = "missing_km"
            viewModel.clearMissingKmFilter()
        }
    }

    var revToEdit by remember { mutableStateOf<RevenueEntry?>(null) }
    var expToEdit by remember { mutableStateOf<ExpenseEntry?>(null) }

    var timeFilter by remember { mutableStateOf("month") } // today, 7days, month, all
    val allRev by viewModel.allRevenueEntries.collectAsState()
    val allExp by viewModel.allExpenseEntries.collectAsState()

    val displayMonth = FormatUtils.formatDisplayMonth(currentMonth)

    val (activeRev, activeExp) = when (timeFilter) {
        "today" -> {
            val today = FormatUtils.formatDbDate(System.currentTimeMillis())
            allRev.filter { it.dateString == today } to allExp.filter { it.dateString == today }
        }
        "7days" -> {
            val cutoff = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
            allRev.filter { it.timestamp >= cutoff } to allExp.filter { it.timestamp >= cutoff }
        }
        "all" -> {
            allRev to allExp
        }
        else -> {
            revenueEntries to expenseEntries
        }
    }

    val combinedItems = mutableListOf<Any>()

    // Filtering
    val filteredRev = when (selectedFilter) {
        "all" -> activeRev
        "has_tip" -> activeRev.filter { (it.tipAmount ?: 0L) > 0L }
        "missing_km" -> activeRev.filter { it.distanceKm == null }
        "expense" -> emptyList()
        else -> {
            val sourceId = selectedFilter.toIntOrNull()
            if (sourceId != null) activeRev.filter { it.sourceId == sourceId } else activeRev
        }
    }

    val filteredExp = when (selectedFilter) {
        "all", "expense" -> activeExp
        else -> emptyList()
    }

    combinedItems.addAll(filteredRev)
    combinedItems.addAll(filteredExp)
    combinedItems.sortByDescending { 
        if (it is RevenueEntry) it.timestamp else if (it is ExpenseEntry) it.timestamp else 0L 
    }

    Column(modifier = Modifier.fillMaxSize().background(BgColor)) {
        // TIME FILTER ROW
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { CustomFilterChip("today", "Hôm nay", timeFilter, primaryColor) { timeFilter = "today" } }
            item { CustomFilterChip("7days", "7 ngày", timeFilter, primaryColor) { timeFilter = "7days" } }
            item { CustomFilterChip("month", "Tháng", timeFilter, primaryColor) { timeFilter = "month" } }
            item { CustomFilterChip("all", "Tất cả", timeFilter, primaryColor) { timeFilter = "all" } }
        }

        if (timeFilter == "month") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF475569)) }
                Text(displayMonth, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                IconButton(onClick = { viewModel.nextMonth() }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF475569)) }
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // TYPE FILTER CHIPS
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CustomFilterChip("all", "Tất cả", selectedFilter, primaryColor) { selectedFilter = "all" }
            }
            sources.forEach { source ->
                item {
                    val count = activeRev.count { it.sourceId == source.id }
                    CustomFilterChip(source.id.toString(), "${source.name} ($count)", selectedFilter, primaryColor) { selectedFilter = source.id.toString() }
                }
            }
            item {
                val mkCount = activeRev.count { it.distanceKm == null }
                CustomFilterChip("missing_km", "Thiếu KM ($mkCount)", selectedFilter, primaryColor) { selectedFilter = "missing_km" }
            }
            item {
                val tipCount = activeRev.count { (it.tipAmount ?: 0L) > 0L }
                CustomFilterChip("has_tip", "Có Tip ($tipCount)", selectedFilter, primaryColor) { selectedFilter = "has_tip" }
            }
            item {
                CustomFilterChip("expense", "Chi phí (${activeExp.size})", selectedFilter, primaryColor) { selectedFilter = "expense" }
            }
        }

        if (combinedItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Không có dữ liệu", color = Color(0xFF94A3B8))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(combinedItems) { item ->
                    if (item is RevenueEntry) {
                        val source = sources.find { it.id == item.sourceId }
                        RevenueRow(item, source) { revToEdit = item }
                    } else if (item is ExpenseEntry) {
                        val cat = categories.find { it.id == item.categoryId }
                        ExpenseRow(item, cat) { expToEdit = item }
                    }
                }
            }
        }
    }

    if (revToEdit != null) {
        EditRevenueSheet(
            entry = revToEdit!!,
            sources = sources,
            primaryColor = primaryColor,
            onDismiss = { revToEdit = null },
            onSave = { updated ->
                viewModel.updateRevenueEntry(updated)
                revToEdit = null
            },
            onDelete = {
                viewModel.deleteRevenue(revToEdit!!.id)
                revToEdit = null
            }
        )
    }

    if (expToEdit != null) {
        EditExpenseSheet(
            entry = expToEdit!!,
            categories = categories,
            primaryColor = primaryColor,
            onDismiss = { expToEdit = null },
            onSave = { updated ->
                viewModel.updateExpenseEntry(updated)
                expToEdit = null
            },
            onDelete = {
                viewModel.deleteExpense(expToEdit!!.id)
                expToEdit = null
            }
        )
    }
}

@Composable
fun CustomFilterChip(id: String, label: String, selectedId: String, primaryColor: Color, onClick: () -> Unit) {
    val isSelected = id == selectedId
    Surface(
        color = if (isSelected) primaryColor else CardSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) primaryColor else CardBorder),
        modifier = Modifier.height(36.dp).clickable { onClick() }
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(label, color = if (isSelected) Color.White else Color(0xFF64748B), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@Composable
fun RevenueRow(entry: RevenueEntry, source: com.example.data.RevenueSource?, onClick: () -> Unit) {
    val color = try { Color(android.graphics.Color.parseColor(source?.colorHex ?: "#94A3B8")) } catch(e: Exception) { Color(0xFF94A3B8) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(source?.name ?: "Khác", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 16.sp)
                }
                Text(FormatUtils.formatCurrency(entry.amount), fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val dateStr = FormatUtils.formatDate(FormatUtils.parseDbDate(entry.dateString)) + " • " + FormatUtils.formatTime(entry.timestamp)
                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                
                val details = mutableListOf<String>()
                details.add("${entry.trips} cuốc")
                if (entry.distanceKm != null) details.add("${String.format("%.1f", entry.distanceKm).replace(".", ",")} km")
                Text(details.joinToString(" • "), style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
            }
            
            if ((entry.tipAmount ?: 0L) > 0L) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text("Tip +${FormatUtils.formatCurrency(entry.tipAmount!!)}", color = Color(0xFF059669), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
            
            if (entry.distanceKm == null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chưa có KM", color = Color(0xFFD97706), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun ExpenseRow(entry: ExpenseEntry, category: com.example.data.ExpenseCategory?, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(category?.name ?: "Chi phí", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 16.sp)
                Text("-${FormatUtils.formatCurrency(entry.amount)}", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            val dateStr = FormatUtils.formatDate(FormatUtils.parseDbDate(entry.dateString)) + " • " + FormatUtils.formatTime(entry.timestamp)
            Text(dateStr, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            
            if (entry.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(entry.note, style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditRevenueSheet(
    entry: RevenueEntry,
    sources: List<com.example.data.RevenueSource>,
    primaryColor: Color,
    onDismiss: () -> Unit,
    onSave: (RevenueEntry) -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        var selectedSourceId by remember { mutableStateOf(entry.sourceId) }
        var amountStr by remember { mutableStateOf(entry.amount.toString()) }
        var tipStr by remember { mutableStateOf(if ((entry.tipAmount ?: 0L) > 0L) entry.tipAmount.toString() else "") }
        var tripsStr by remember { mutableStateOf(entry.trips.toString()) }
        var distanceStr by remember { mutableStateOf(entry.distanceKm?.toString() ?: "") }
        var note by remember { mutableStateOf(entry.note) }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 32.dp).imePadding().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Sửa doanh thu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                TextButton(onClick = onDelete) { Text("XÓA", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold) }
            }
            
            // Source Segmented
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
                            Text(source.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color(0xFF0F172A) else Color(0xFF64748B), fontSize = 14.sp)
                        }
                    }
                }
            }
            
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
                label = { Text("Số tiền thực nhận (đ)") },
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
                    label = { Text("Số cuốc") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = distanceStr,
                    onValueChange = { distanceStr = it.replace(",", ".") },
                    label = { Text("Số KM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }
            
            OutlinedTextField(
                value = tipStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) tipStr = newValue },
                label = { Text("Tiền tip") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            Button(
                onClick = {
                    val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    val tip = tipStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    val trips = tripsStr.toIntOrNull()
                    val dist = distanceStr.toFloatOrNull()
                    if (amount != null && trips != null && trips >= 1) {
                        onSave(entry.copy(
                            sourceId = selectedSourceId,
                            amount = amount,
                            tipAmount = tip ?: 0L,
                            trips = trips,
                            distanceKm = dist,
                            note = note
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L && (tripsStr.toIntOrNull() ?: 0) >= 1
            ) {
                Text("LƯU THAY ĐỔI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditExpenseSheet(
    entry: ExpenseEntry,
    categories: List<com.example.data.ExpenseCategory>,
    primaryColor: Color,
    onDismiss: () -> Unit,
    onSave: (ExpenseEntry) -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        var selectedCategoryId by remember { mutableStateOf(entry.categoryId) }
        var amountStr by remember { mutableStateOf(entry.amount.toString()) }
        var note by remember { mutableStateOf(entry.note) }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 32.dp).imePadding().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Sửa chi phí", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                TextButton(onClick = onDelete) { Text("XÓA", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold) }
            }
            
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
                label = { Text("Số tiền (đ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            
            Button(
                onClick = {
                    val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    if (amount != null) {
                        onSave(entry.copy(
                            categoryId = selectedCategoryId,
                            amount = amount,
                            note = note
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L
            ) {
                Text("LƯU THAY ĐỔI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
