package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseCategory
import com.example.data.RevenueEntry
import com.example.data.RevenueSource
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

@Composable
fun TodayScreen(
    viewModel: LedgerViewModel,
    onNavigateToMissingKm: () -> Unit
) {
    val currentDate by viewModel.currentDate.collectAsState()
    val totalRevenue by viewModel.todaysTotalRevenue.collectAsState()
    val totalTip by viewModel.todaysTotalTip.collectAsState()
    val totalExpense by viewModel.todaysTotalExpense.collectAsState()
    val netIncome by viewModel.todaysNetIncome.collectAsState()
    val totalTrips by viewModel.todaysTotalTrips.collectAsState()
    val totalDistance by viewModel.todaysTotalDistance.collectAsState()
    val revenueEntries by viewModel.todaysRevenueEntries.collectAsState()
    val sources by viewModel.allRevenueSources.collectAsState()
    val activeSources by viewModel.activeRevenueSources.collectAsState()
    val activeCategories by viewModel.activeExpenseCategories.collectAsState()

    var showAddRevenue by remember { mutableStateOf(false) }
    var showAddExpense by remember { mutableStateOf(false) }
    
    val bgHex by viewModel.cardBgColor.collectAsState()
    val incomeHex by viewModel.incomeColor.collectAsState()
    val revHex by viewModel.revenueColor.collectAsState()
    val expHex by viewModel.expenseColor.collectAsState()
    val tipHex by viewModel.tipColor.collectAsState()

    val missingKmCount = revenueEntries.count { it.distanceKm == null }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Navigation
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousDay() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Ngày trước")
            }
            Text(
                text = FormatUtils.formatDisplayDate(currentDate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.nextDay() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Ngày sau")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                HeroIncomeCard(
                    netIncome = netIncome,
                    totalRevenue = totalRevenue,
                    totalTip = totalTip,
                    totalExpense = totalExpense,
                    totalTrips = totalTrips,
                    totalDistance = totalDistance,
                    missingKmCount = missingKmCount,
                    onNavigateToMissingKm = onNavigateToMissingKm,
                    bgHex = bgHex,
                    incomeHex = incomeHex,
                    revHex = revHex,
                    expHex = expHex,
                    tipHex = tipHex
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { showAddRevenue = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Doanh thu")
                    }
                    Button(
                        onClick = { showAddExpense = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chi phí")
                    }
                }
            }

            item {
                Text("CƠ CẤU DOANH THU", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (revenueEntries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        Text("Chưa có dữ liệu", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    val grouped = revenueEntries.groupBy { it.sourceId }
                    val sortedGroups = grouped.mapValues { it.value.sumOf { r -> r.amount } }.toList().sortedByDescending { it.second }
                    
                    // Progress bar distribution
                    Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {
                        sortedGroups.forEach { (sourceId, amount) ->
                            val weight = amount.toFloat() / totalRevenue.toFloat()
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
                            val pct = if (totalRevenue > 0) (amount.toDouble() / totalRevenue * 100) else 0.0
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
        }
    }

    if (showAddRevenue) {
        AddRevenueSheet(
            sources = activeSources,
            onDismiss = { showAddRevenue = false },
            onSave = { srcId, amt, tip, trips, dist, note ->
                viewModel.addRevenue(srcId, amt, tip, trips, null, dist, note)
                showAddRevenue = false
            }
        )
    }

    if (showAddExpense) {
        AddExpenseSheet(
            categories = activeCategories,
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
    totalRevenue: Long,
    totalTip: Long,
    totalExpense: Long,
    totalTrips: Int,
    totalDistance: Float,
    missingKmCount: Int,
    onNavigateToMissingKm: () -> Unit,
    bgHex: String,
    incomeHex: String,
    revHex: String,
    expHex: String,
    tipHex: String
) {
    val bgColor = try { Color(android.graphics.Color.parseColor(bgHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primaryContainer }
    val autoOnColor = if (bgColor.luminance() > 0.5f) Color.Black else Color.White
    
    val incomeColor = if (incomeHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(incomeHex)) } catch (e: Exception) { autoOnColor } else autoOnColor
    val revColor = if (revHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(revHex)) } catch (e: Exception) { autoOnColor } else autoOnColor
    val expColor = if (expHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expHex)) } catch (e: Exception) { Color(0xFFF05D5E) } else Color(0xFFF05D5E)
    val tipColor = if (tipHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(tipHex)) } catch (e: Exception) { Color(0xFFF59E0B) } else Color(0xFFF59E0B)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("THU NHẬP HÔM NAY", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
            Text(
                text = if (totalTrips == 0 && netIncome == 0L) "0 đ" else FormatUtils.formatCurrency(netIncome),
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 36.sp),
                fontWeight = FontWeight.Bold,
                color = incomeColor,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            if (totalTrips == 0) {
                Text("Chưa có cuốc nào", color = autoOnColor.copy(alpha = 0.6f))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Doanh thu", style = MaterialTheme.typography.labelSmall, color = autoOnColor.copy(alpha = 0.7f))
                        Text(FormatUtils.formatCurrency(totalRevenue).replace(" đ", ""), fontWeight = FontWeight.Bold, color = revColor, style = MaterialTheme.typography.bodyLarge)
                    }
                    if (totalTip > 0) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tip", style = MaterialTheme.typography.labelSmall, color = autoOnColor.copy(alpha = 0.7f))
                            Text("+${FormatUtils.formatCurrency(totalTip).replace(" đ", "")}", fontWeight = FontWeight.Bold, color = tipColor, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Chi phí", style = MaterialTheme.typography.labelSmall, color = autoOnColor.copy(alpha = 0.7f))
                        Text(if(totalExpense > 0) "-${FormatUtils.formatCurrency(totalExpense).replace(" đ", "")}" else "0", fontWeight = FontWeight.Bold, color = expColor, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 16.dp), color = autoOnColor.copy(alpha = 0.1f))
                
                val avgRev = if (totalTrips > 0) totalRevenue / totalTrips else 0L
                val kmText = if (missingKmCount > 0) "${String.format("%.1f", totalDistance).replace(".", ",")} km*" else "${String.format("%.1f", totalDistance).replace(".", ",")} km"
                Text(
                    text = "$totalTrips cuốc • $kmText • TB ${FormatUtils.formatCurrency(avgRev)}/cuốc",
                    style = MaterialTheme.typography.bodySmall,
                    color = autoOnColor.copy(alpha = 0.9f)
                )
                
                if (missingKmCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { onNavigateToMissingKm() }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("⚠ $missingKmCount cuốc chưa có KM", color = Color(0xFFD97706), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRevenueSheet(
    sources: List<RevenueSource>,
    onDismiss: () -> Unit,
    onSave: (Int, Long, Long?, Int, Float?, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var selectedSourceId by remember { mutableStateOf(sources.firstOrNull()?.id ?: 0) }
        var amountStr by remember { mutableStateOf("") }
        var tipStr by remember { mutableStateOf("") }
        var tripsStr by remember { mutableStateOf("1") }
        var distanceStr by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
                .imePadding().verticalScroll(rememberScrollState())
        ) {
            Text("NHẬP DOANH THU", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sources.forEach { source ->
                    val color = try { Color(android.graphics.Color.parseColor(source.colorHex)) } catch (e: Exception) { Color.Gray }
                    FilterChip(
                        selected = selectedSourceId == source.id,
                        onClick = { selectedSourceId = source.id },
                        label = { Text(source.name) },
                        leadingIcon = { Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color)) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
                label = { Text("Số tiền thực nhận (đ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = tripsStr,
                onValueChange = { tripsStr = it },
                label = { Text("Số cuốc") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = distanceStr,
                onValueChange = { distanceStr = it.replace(",", ".") },
                label = { Text("Số KM") },
                placeholder = { Text("Không bắt buộc") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = tipStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) tipStr = newValue },
                label = { Text("Tiền tip (Tùy chọn)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
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
                    val tip = tipStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                    val trips = tripsStr.toIntOrNull()
                    val dist = distanceStr.toFloatOrNull()
                    if (amount != null && trips != null && trips >= 1 && selectedSourceId != 0) {
                        onSave(selectedSourceId, amount, tip, trips, dist, note)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L && (tripsStr.toIntOrNull() ?: 0) >= 1
            ) {
                Text("LƯU DOANH THU", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp).imePadding().verticalScroll(rememberScrollState())
        ) {
            Text("NHẬP CHI PHÍ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.name) },
                        leadingIcon = {
                            val iconRes = when (category.iconName) {
                                "ev_station" -> Icons.Filled.EvStation
                                "restaurant" -> Icons.Filled.Restaurant
                                "local_parking" -> Icons.Filled.LocalParking
                                "add_road" -> Icons.Filled.AddRoad
                                "local_car_wash" -> Icons.Filled.LocalCarWash
                                "build" -> Icons.Filled.Build
                                "phone_android" -> Icons.Filled.PhoneAndroid
                                else -> Icons.Filled.MoreHoriz
                            }
                            Icon(iconRes, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
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
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L
            ) {
                Text("LƯU CHI PHÍ", fontWeight = FontWeight.Bold)
            }
        }
    }
}
