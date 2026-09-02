package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ExpenseCategory
import com.example.data.RevenueSource
import com.example.ui.viewmodels.LedgerViewModel
import kotlinx.coroutines.launch

val predefinedColors = listOf(
    "#16A34A", // Emerald
    "#3B82F6", // Blue
    "#8B5CF6", // Violet
    "#F59E0B", // Orange
    "#E11D48", // Rose
    "#14B8A6", // Teal
    "#6366F1", // Indigo
    "#D97706"  // Amber
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSourcesSheet(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sources by viewModel.activeRevenueSources.collectAsState(initial = emptyList())
    var editingSource by remember { mutableStateOf<RevenueSource?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("QUẢN LÝ NGUỒN THU", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
            
            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(sources) { source ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editingSource = source }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(source.colorHex))))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(source.name, fontWeight = FontWeight.Bold)
                            Text(if (source.isActive) "Đang sử dụng" else "Đã ẩn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                }
                item {
                    TextButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thêm nguồn thu")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf(predefinedColors[0]) }
        val isDuplicateColor = sources.any { it.colorHex == selectedColor }
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm nguồn thu") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên nguồn thu") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chọn màu:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        predefinedColors.take(4).forEach { colorHex ->
                            ColorDot(colorHex, selectedColor == colorHex) { selectedColor = colorHex }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        predefinedColors.drop(4).take(4).forEach { colorHex ->
                            ColorDot(colorHex, selectedColor == colorHex) { selectedColor = colorHex }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isDuplicateColor) {
                        Text("Màu này đang được dùng cho nguồn khác. Bạn vẫn muốn sử dụng?", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addRevenueSource(name, selectedColor)
                        showAddDialog = false
                    }
                }) { Text("LƯU") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("HỦY") }
            }
        )
    }

    editingSource?.let { source ->
        EditSourceDialog(
            source = source,
            viewModel = viewModel,
            onDismiss = { editingSource = null }
        )
    }
}

@Composable
fun ColorDot(colorHex: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(android.graphics.Color.parseColor(colorHex)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.White))
        }
    }
}

@Composable
fun EditSourceDialog(source: RevenueSource, viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sources by viewModel.activeRevenueSources.collectAsState(initial = emptyList())
    var name by remember { mutableStateOf(source.name) }
    var selectedColor by remember { mutableStateOf(source.colorHex) }
    var isActive by remember { mutableStateOf(source.isActive) }
    val isDuplicateColor = sources.any { it.colorHex == selectedColor && it.id != source.id }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa nguồn thu") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên nguồn thu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Chọn màu:", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    predefinedColors.take(4).forEach { colorHex ->
                        ColorDot(colorHex, selectedColor == colorHex) { selectedColor = colorHex }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    predefinedColors.drop(4).take(4).forEach { colorHex ->
                        ColorDot(colorHex, selectedColor == colorHex) { selectedColor = colorHex }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isDuplicateColor) {
                    Text("Màu này đang được dùng cho nguồn khác.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                    Text("Đang sử dụng")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    viewModel.updateRevenueSource(source.copy(name = name, colorHex = selectedColor, isActive = isActive))
                    onDismiss()
                }
            }) { Text("LƯU") }
        },
        dismissButton = {
            TextButton(onClick = { 
                viewModel.deleteRevenueSource(source)
                onDismiss()
            }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("XÓA") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesSheet(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val categories by viewModel.activeExpenseCategories.collectAsState(initial = emptyList())
    var editingCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("DANH MỤC CHI PHÍ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
            
            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(categories) { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editingCategory = cat }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cat.name, fontWeight = FontWeight.Bold)
                            Text(if (cat.isActive) "Đang sử dụng" else "Đã ẩn", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                }
                item {
                    TextButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thêm danh mục")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm danh mục") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên danh mục") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addExpenseCategory(name, "more_horiz")
                        showAddDialog = false
                    }
                }) { Text("LƯU") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("HỦY") }
            }
        )
    }

    editingCategory?.let { cat ->
        EditCategoryDialog(
            category = cat,
            viewModel = viewModel,
            onDismiss = { editingCategory = null }
        )
    }
}

@Composable
fun EditCategoryDialog(category: ExpenseCategory, viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(category.name) }
    var isActive by remember { mutableStateOf(category.isActive) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa danh mục") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên danh mục") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                    Text("Đang sử dụng")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    viewModel.updateExpenseCategory(category.copy(name = name, isActive = isActive))
                    onDismiss()
                }
            }) { Text("LƯU") }
        },
        dismissButton = {
            TextButton(onClick = { 
                viewModel.deleteExpenseCategory(category)
                onDismiss()
            }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("XÓA") }
        }
    )
}
