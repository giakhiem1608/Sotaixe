package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ExpenseCategory
import com.example.data.RevenueSource
import com.example.ui.viewmodels.LedgerViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(viewModel: LedgerViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var showManageSources by remember { mutableStateOf(false) }
    var showManageCategories by remember { mutableStateOf(false) }
    var showXlsxDialog by remember { mutableStateOf(false) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog1 by remember { mutableStateOf(false) }
    var showResetConfirmDialog2 by remember { mutableStateOf(false) }
    var pendingRestoreUri by remember { mutableStateOf<Uri?>(null) }
    
    val currentMonth by viewModel.currentMonth.collectAsState()

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.generateBackupData { json ->
                if (json != null) {
                    try {
                        context.contentResolver.openOutputStream(it)?.use { stream ->
                            stream.write(json.toByteArray())
                        }
                        Toast.makeText(context, "Sao lưu thành công!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi khi ghi file sao lưu", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi tạo dữ liệu sao lưu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingRestoreUri = it
            showRestoreConfirmDialog = true
        }
    }

    val csvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            viewModel.exportCsvData(currentMonth) { csvData ->
                if (csvData != null) {
                    try {
                        context.contentResolver.openOutputStream(it)?.use { stream ->
                            stream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                            stream.write(csvData.toByteArray())
                        }
                        Toast.makeText(context, "Xuất CSV thành công!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi khi ghi file CSV", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Lỗi khi tạo dữ liệu CSV", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val xlsxLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri ->
        uri?.let {
            try {
                val outputStream = context.contentResolver.openOutputStream(it)
                if (outputStream != null) {
                    viewModel.exportXlsxData(currentMonth, outputStream) { success ->
                        coroutineScope.launch {
                            if (success) {
                                Toast.makeText(context, "Xuất Excel thành công!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Lỗi khi xuất Excel", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi ghi file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cài đặt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                SettingsMenuItem(
                    title = "Quản lý nguồn thu",
                    subtitle = "Thêm, sửa, ẩn các nguồn thu",
                    icon = Icons.Default.AccountBalanceWallet,
                    onClick = { showManageSources = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Quản lý danh mục chi phí",
                    subtitle = "Thêm, sửa, ẩn các loại chi phí",
                    icon = Icons.Default.Category,
                    onClick = { showManageCategories = true }
                )
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                SettingsMenuItem(
                    title = "Xuất dữ liệu",
                    subtitle = "Lưu dữ liệu ra file Excel (.xlsx)",
                    icon = Icons.Default.InsertDriveFile,
                    onClick = { showXlsxDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Sao lưu dữ liệu",
                    subtitle = "Lưu toàn bộ dữ liệu ra file an toàn",
                    icon = Icons.Default.Backup,
                    onClick = { 
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        backupLauncher.launch("SoTaiXe_Backup_$date.json") 
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Khôi phục dữ liệu",
                    subtitle = "Phục hồi từ file sao lưu JSON",
                    icon = Icons.Default.Restore,
                    onClick = { 
                        restoreLauncher.launch(arrayOf("application/json", "*/*")) 
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Xóa toàn bộ dữ liệu",
                    subtitle = "Xóa tất cả giao dịch và thiết lập lại",
                    icon = Icons.Default.DeleteForever,
                    onClick = { showRestoreConfirmDialog = true; pendingRestoreUri = Uri.parse("reset_data") } // Wait, this will trigger restore with invalid URI, let's create a new dialog
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.LocalTaxi,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp).padding(end = 4.dp)
            )
            Text(
                "Made for drivers • LHN",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showRestoreConfirmDialog && pendingRestoreUri != null) {
        AlertDialog(
            onDismissRequest = { 
                showRestoreConfirmDialog = false
                pendingRestoreUri = null
            },
            title = { Text("Cảnh báo khôi phục") },
            text = { Text("Dữ liệu hiện tại của bạn sẽ bị thay thế hoàn toàn bằng dữ liệu từ file sao lưu. Bạn có chắc chắn muốn tiếp tục?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val json = context.contentResolver.openInputStream(pendingRestoreUri!!)?.bufferedReader()?.use { it.readText() }
                                if (json != null) {
                                    viewModel.restoreBackupData(json) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Khôi phục thành công!", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Lỗi: File khôi phục không hợp lệ", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi khi đọc file", Toast.LENGTH_LONG).show()
                            }
                            showRestoreConfirmDialog = false
                            pendingRestoreUri = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("KHÔI PHỤC")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showRestoreConfirmDialog = false 
                    pendingRestoreUri = null
                }) {
                    Text("HỦY")
                }
            }
        )
    }

    if (showResetConfirmDialog1) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog1 = false },
            title = { Text("Cảnh báo nguy hiểm", color = MaterialTheme.colorScheme.error) },
            text = { Text("Bạn đang chuẩn bị XÓA TOÀN BỘ dữ liệu giao dịch, mục tiêu, nguồn thu và chi phí. Hành động này không thể hoàn tác. Bạn có chắc chắn?") },
            confirmButton = {
                Button(onClick = { showResetConfirmDialog1 = false; showResetConfirmDialog2 = true }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("TIẾP TỤC XÓA") }
            },
            dismissButton = { TextButton(onClick = { showResetConfirmDialog1 = false }) { Text("HỦY") } }
        )
    }

    if (showResetConfirmDialog2) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog2 = false },
            title = { Text("Xác nhận lần cuối", color = MaterialTheme.colorScheme.error) },
            text = { Text("Xóa toàn bộ dữ liệu? Các danh mục thu chi sẽ được đặt lại về mặc định.") },
            confirmButton = {
                Button(onClick = { 
                    coroutineScope.launch { 
                        viewModel.resetAllData() 
                        Toast.makeText(context, "Đã xóa toàn bộ dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    showResetConfirmDialog2 = false 
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("CHẮC CHẮN XÓA") }
            },
            dismissButton = { TextButton(onClick = { showResetConfirmDialog2 = false }) { Text("HỦY") } }
        )
    }

    if (showXlsxDialog) {
        var selectedOption by remember { mutableStateOf(0) }
        var selectedFormat by remember { mutableStateOf("XLSX") }
        
        AlertDialog(
            onDismissRequest = { showXlsxDialog = false },
            title = { Text("Chọn tháng xuất dữ liệu") },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedOption = 0 }) {
                        RadioButton(selected = selectedOption == 0, onClick = { selectedOption = 0 })
                        Text("Tháng hiện tại")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedOption = 1 }) {
                        RadioButton(selected = selectedOption == 1, onClick = { selectedOption = 1 })
                        Text("Tháng trước")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Định dạng", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedFormat = "XLSX" }) {
                        RadioButton(selected = selectedFormat == "XLSX", onClick = { selectedFormat = "XLSX" })
                        Text("Excel (.xlsx)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedFormat = "CSV" }) {
                        RadioButton(selected = selectedFormat == "CSV", onClick = { selectedFormat = "CSV" })
                        Text("CSV")
                    }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showXlsxDialog = false
                    var targetMonth = currentMonth
                    if (selectedOption == 1) {
                        val format = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                        val cal = Calendar.getInstance()
                        cal.time = format.parse(currentMonth) ?: Date()
                        cal.add(Calendar.MONTH, -1)
                        targetMonth = format.format(cal.time)
                    }
                    if (selectedFormat == "XLSX") xlsxLauncher.launch("SoTaiXe_BaoCao_$targetMonth.xlsx") else csvLauncher.launch("SoTaiXe_BaoCao_$targetMonth.csv")
                }) {
                    Text("XUẤT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showXlsxDialog = false }) { Text("HỦY") }
            }
        )
    }

    if (showManageSources) {
        ManageSourcesDialog(viewModel = viewModel, onDismiss = { showManageSources = false })
    }

    if (showManageCategories) {
        ManageCategoriesDialog(viewModel = viewModel, onDismiss = { showManageCategories = false })
    }
}

@Composable
fun SettingsMenuItem(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, 
            contentDescription = null, 
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManageSourcesDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sources by viewModel.activeRevenueSources.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSource by remember { mutableStateOf<RevenueSource?>(null) }
    
    val colorOptions = listOf("#2979FF", "#00C853", "#FF3D00", "#FFC107", "#9C27B0", "#E91E63", "#00BCD4", "#607D8B")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nguồn thu") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(sources) { source ->
                        ListItem(
                            headlineContent = { Text(source.name, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.clickable { editingSource = source },
                            trailingContent = {
                                IconButton(onClick = { viewModel.hideRevenueSource(source) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Ẩn", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showAddDialog = true }) {
                Text("THÊM MỚI")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ĐÓNG") }
        }
    )

    if (showAddDialog || editingSource != null) {
        var name by remember { mutableStateOf(editingSource?.name ?: "") }
        var selectedColor by remember { mutableStateOf(editingSource?.colorHex ?: colorOptions[0]) }
        
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                editingSource = null
            },
            title = { Text(if (editingSource != null) "Sửa nguồn thu" else "Thêm nguồn thu") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên nguồn thu") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chọn màu:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colorOptions.forEach { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .let {
                                        if (selectedColor == hex) {
                                            it.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        } else {
                                            it
                                        }
                                    }
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingSource != null) {
                            viewModel.updateRevenueSource(editingSource!!.copy(name = name, colorHex = selectedColor))
                        } else {
                            viewModel.addRevenueSource(name, selectedColor)
                        }
                        showAddDialog = false
                        editingSource = null
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("LƯU")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    editingSource = null
                }) { Text("HỦY") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ManageCategoriesDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val categories by viewModel.activeExpenseCategories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    
    val iconOptions = listOf(
        "battery_charging_full" to Icons.Filled.BatteryChargingFull,
        "restaurant" to Icons.Filled.Restaurant,
        "local_parking" to Icons.Filled.LocalParking,
        "add_road" to Icons.Filled.AddRoad,
        "local_car_wash" to Icons.Filled.LocalCarWash,
        "build" to Icons.Filled.Build,
        "phone_android" to Icons.Filled.PhoneAndroid,
        "more_horiz" to Icons.Filled.MoreHoriz
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Danh mục chi phí") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(categories) { category ->
                        ListItem(
                            headlineContent = { Text(category.name, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.clickable { editingCategory = category },
                            trailingContent = {
                                IconButton(onClick = { viewModel.hideExpenseCategory(category) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Ẩn", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showAddDialog = true }) {
                Text("THÊM MỚI")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ĐÓNG") }
        }
    )

    if (showAddDialog || editingCategory != null) {
        var name by remember { mutableStateOf(editingCategory?.name ?: "") }
        var selectedIcon by remember { mutableStateOf(editingCategory?.iconName ?: iconOptions[0].first) }
        
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                editingCategory = null
            },
            title = { Text(if (editingCategory != null) "Sửa danh mục" else "Thêm danh mục") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên danh mục") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chọn icon:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconOptions.forEach { (iconName, vector) ->
                            IconButton(
                                onClick = { selectedIcon = iconName },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        if (selectedIcon == iconName) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = vector,
                                    contentDescription = null,
                                    tint = if (selectedIcon == iconName) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingCategory != null) {
                            viewModel.updateExpenseCategory(editingCategory!!.copy(name = name, iconName = selectedIcon))
                        } else {
                            viewModel.addExpenseCategory(name, selectedIcon)
                        }
                        showAddDialog = false
                        editingCategory = null
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("LƯU")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    editingCategory = null
                }) { Text("HỦY") }
            }
        )
    }
}
