package com.example.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntry
import com.example.data.RevenueEntry
import com.example.data.RevenueSource
import com.example.utils.CurrencyVisualTransformation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditRevenueSheet(
    entry: RevenueEntry,
    sources: List<RevenueSource>,
    onDismiss: () -> Unit,
    onSave: (RevenueEntry) -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var selectedSourceId by remember { mutableStateOf(entry.sourceId) }
        var amountStr by remember { mutableStateOf(entry.amount.toString()) }
        var tripsStr by remember { mutableStateOf(entry.trips.toString()) }
        var distanceStr by remember { mutableStateOf(entry.distanceKm?.toString() ?: "") }
        var note by remember { mutableStateOf(entry.note) }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
                .imePadding().verticalScroll(rememberScrollState())
        ) {
            Text("Sửa doanh thu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Source selector
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    if (newValue.all { it.isDigit() }) amountStr = newValue
                },
                label = { Text("Số tiền thực nhận (đ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CurrencyVisualTransformation(),
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
                value = distanceStr,
                onValueChange = { distanceStr = it.replace(",", ".") },
                label = { Text("Số km (Không bắt buộc)") },
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
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("XÓA", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("HỦY", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = {
                        val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                        val trips = tripsStr.toIntOrNull()
                        if (amount != null && trips != null && trips >= 1 && selectedSourceId != 0) {
                            onSave(entry.copy(
                                sourceId = selectedSourceId,
                                amount = amount,
                                trips = trips,
                                note = note
                            ))
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L && tripsStr.isNotBlank() && (tripsStr.toIntOrNull() ?: 0) >= 1
                ) {
                    Text("LƯU", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xóa giao dịch này?") },
            text = { Text("Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); onDismiss() }) {
                    Text("XÓA", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("HỦY")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditExpenseSheet(
    entry: ExpenseEntry,
    categories: List<ExpenseCategory>,
    onDismiss: () -> Unit,
    onSave: (ExpenseEntry) -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var selectedCategoryId by remember { mutableStateOf(entry.categoryId) }
        var amountStr by remember { mutableStateOf(entry.amount.toString()) }
        var note by remember { mutableStateOf(entry.note) }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
                .imePadding().verticalScroll(rememberScrollState())
        ) {
            Text("Sửa chi phí", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Category selector
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) amountStr = newValue
                },
                label = { Text("Số tiền (đ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CurrencyVisualTransformation(),
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
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("XÓA", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("HỦY", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = {
                        val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                        if (amount != null && selectedCategoryId != 0) {
                            onSave(entry.copy(
                                categoryId = selectedCategoryId,
                                amount = amount,
                                note = note
                            ))
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
    
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xóa giao dịch này?") },
            text = { Text("Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); onDismiss() }) {
                    Text("XÓA", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("HỦY")
                }
            }
        )
    }
}
