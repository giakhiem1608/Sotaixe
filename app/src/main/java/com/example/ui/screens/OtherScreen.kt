package com.example.ui.screens


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


const val APP_VERSION = "1.0 (Patch HN10)"

@Composable
fun OtherScreen(viewModel: LedgerViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var showExportDialog by remember { mutableStateOf(false) }
    var showManageSources by remember { mutableStateOf(false) }
    var showManageCategories by remember { mutableStateOf(false) }
    var showThemeSettings by remember { mutableStateOf(false) }
    var showAppBranding by remember { mutableStateOf(false) }
    var showAppInfoSheet by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog2 by remember { mutableStateOf(false) }

    val primaryHex by viewModel.primaryColorHex.collectAsState()
    val primaryColor = try { Color(android.graphics.Color.parseColor(primaryHex)) } catch(e: Exception) { Color(0xFF0284C7) }

    // Launcher for file creation (backup)
    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.generateBackupData { json ->
                if (json != null) {
                    try {
                        context.contentResolver.openOutputStream(it)?.use { stream ->
                            stream.write(json.toByteArray())
                        }
                        Toast.makeText(context, "Sao lưu dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi khi lưu file", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Lỗi tạo dữ liệu sao lưu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val json = stream.bufferedReader().use { reader -> reader.readText() }
                    viewModel.restoreBackupData(json) { success ->
                        if (success) {
                            Toast.makeText(context, "Khôi phục dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Lỗi dữ liệu", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi đọc file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CÀI ĐẶT", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // QUẢN LÝ
            SettingsGroup(title = "Quản lý") {
                SettingsMenuItem("Nguồn thu", "Grab, Xanh SM, Khách ngoài...", Icons.Filled.DirectionsCar, onClick = { showManageSources = true })
                HorizontalDivider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                SettingsMenuItem("Danh mục chi phí", "Sạc xe, Ăn uống, Gửi xe...", Icons.Filled.Category, onClick = { showManageCategories = true })
            }
            
            // GIAO DIỆN
            SettingsGroup(title = "Giao diện") {
                SettingsMenuItem("Chủ đề & Màu sắc", "Tùy chỉnh thẻ thu nhập & màu sắc", Icons.Filled.Palette, onClick = { showThemeSettings = true })
                HorizontalDivider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                SettingsMenuItem("Thương hiệu ứng dụng", "Đổi icon, thiết lập nhận diện", Icons.Filled.DashboardCustomize, onClick = { showAppBranding = true })
            }

            // DỮ LIỆU
            SettingsGroup(title = "Dữ liệu") {
                SettingsMenuItem("Xuất báo cáo Excel", "Lưu file .xlsx", Icons.Filled.TableChart, onClick = { showExportDialog = true })
                HorizontalDivider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                SettingsMenuItem("Sao lưu dữ liệu", "Tạo bản sao lưu an toàn", Icons.Filled.Backup, onClick = {
                    val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
                    createBackupLauncher.launch("BaBon_Backup_${sdf.format(Date())}.json")
                })
                HorizontalDivider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                SettingsMenuItem("Khôi phục dữ liệu", "Phục hồi từ bản sao lưu", Icons.Filled.SettingsBackupRestore, onClick = {
                    restoreBackupLauncher.launch(arrayOf("application/json", "*/*"))
                })
                HorizontalDivider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
                SettingsMenuItem("Xóa toàn bộ dữ liệu", "Reset ứng dụng", Icons.Filled.DeleteForever, textColor = Color(0xFFDC2626), onClick = { showResetConfirmDialog = true })
            }

            // HỆ THỐNG
            SettingsGroup(title = "Hệ thống") {
                SettingsMenuItem("Thông tin phần mềm", "Phiên bản $APP_VERSION", Icons.Filled.Info, onClick = { showAppInfoSheet = true })
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showExportDialog) {
        ExportExcelDialog(
            onDismiss = { showExportDialog = false },
            viewModel = viewModel,
            primaryColor = primaryColor
        )
    }

    if (showManageSources) {
        ManageSourcesSheet(viewModel = viewModel, onDismiss = { showManageSources = false }, primaryColor = primaryColor)
    }

    if (showManageCategories) {
        ManageCategoriesSheet(viewModel = viewModel, onDismiss = { showManageCategories = false }, primaryColor = primaryColor)
    }
    
    if (showThemeSettings) {
        ThemeSettingsSheet(themeManager = viewModel.themeManager, onDismiss = { showThemeSettings = false })
    }
    
    if (showAppBranding) {
        AppBrandingSheet(onDismiss = { showAppBranding = false }, primaryColor = primaryColor)
    }

    if (showAppInfoSheet) {
        AppInfoSheet(onDismiss = { showAppInfoSheet = false })
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Cảnh báo xóa dữ liệu", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa TOÀN BỘ dữ liệu giao dịch và cài đặt không? Hành động này không thể hoàn tác.", color = Color(0xFF475569)) },
            confirmButton = {
                Button(onClick = {
                    showResetConfirmDialog = false
                    showResetConfirmDialog2 = true
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))) {
                    Text("XÓA", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) { Text("HỦY", color = Color(0xFF64748B), fontWeight = FontWeight.Bold) }
            }
        )
    }

    if (showResetConfirmDialog2) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog2 = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Xác nhận cuối cùng", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold) },
            text = { Text("Dữ liệu sẽ bị mất vĩnh viễn. Hãy chắc chắn bạn đã sao lưu nếu cần thiết.", color = Color(0xFF475569)) },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.resetAllData()
                        Toast.makeText(context, "Đã xóa toàn bộ dữ liệu", Toast.LENGTH_SHORT).show()
                        showResetConfirmDialog2 = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))) {
                    Text("XÓA VĨNH VIỄN", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog2 = false }) { Text("HỦY", color = Color(0xFF64748B), fontWeight = FontWeight.Bold) }
            }
        )
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, CardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun SettingsMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    textColor: Color = Color(0xFF0F172A),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = if (textColor == Color(0xFF0F172A)) Color(0xFF64748B) else textColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = textColor, fontSize = 16.sp)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = if (textColor == Color(0xFF0F172A)) Color(0xFF64748B) else textColor.copy(alpha = 0.8f))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCBD5E1))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss, 
        sheetState = sheetState,
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("BA BON", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text("Quản lý thu nhập tài xế", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
            
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp)) {
                Text("Version $APP_VERSION", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFF475569))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Developed by", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
            Text("BA BON", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBrandingSheet(onDismiss: () -> Unit, primaryColor: Color) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("THƯƠNG HIỆU ỨNG DỤNG", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier.size(96.dp).clip(RoundedCornerShape(24.dp)).background(primaryColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tên hiển thị: BA BON", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Preset Icon", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))
            
            val icons = listOf(Icons.Default.DirectionsCar, Icons.Default.Route, Icons.Default.Wallet, Icons.Default.PieChart)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                icons.forEachIndexed { i, icon ->
                    val isSelected = i == 0
                    Box(
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) primaryColor else Color(0xFFF1F5F9))
                            .clickable { /* Select icon (UI only, system icon changes at build time) */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF64748B), modifier = Modifier.size(32.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lưu ý: Custom Launcher Icon thực sự (trên màn hình chính của điện thoại) chỉ được tạo và áp dụng ở thời điểm Build-time do giới hạn của Android Platform.", 
                    style = MaterialTheme.typography.bodySmall, color = Color(0xFFB45309), modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun ExportExcelDialog(onDismiss: () -> Unit, viewModel: LedgerViewModel, primaryColor: Color) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var selectedRange by remember { mutableStateOf("Hôm nay") }
    val ranges = listOf("Hôm nay", "7 ngày qua", "Tháng này", "Tháng trước", "Tùy chỉnh")
    
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    viewModel.exportXlsxDataRange("2000-01-01", "2100-01-01", selectedRange, stream) { success ->
                        if (success) {
                            coroutineScope.launch { Toast.makeText(context, "Xuất Excel thành công!", Toast.LENGTH_SHORT).show() }
                        } else {
                            coroutineScope.launch { Toast.makeText(context, "Lỗi xuất file", Toast.LENGTH_SHORT).show() }
                        }
                    }
                }
            } catch (e: Exception) {
                coroutineScope.launch { Toast.makeText(context, "Lỗi khi ghi file", Toast.LENGTH_SHORT).show() }
            }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        shape = RoundedCornerShape(24.dp),
        title = { Text("Xuất báo cáo Excel", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A)) },
        text = {
            Column {
                ranges.forEach { range ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { selectedRange = range }.padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedRange == range, onClick = { selectedRange = range }, colors = RadioButtonDefaults.colors(selectedColor = primaryColor))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(range, color = Color(0xFF0F172A), fontWeight = if (selectedRange == range) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val filename = "BaoCao_BaBon_${sdf.format(Date())}.xlsx"
                exportLauncher.launch(filename)
                onDismiss()
            }, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) {
                Text("XUẤT FILE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("HỦY", color = Color(0xFF64748B), fontWeight = FontWeight.Bold) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSourcesSheet(viewModel: LedgerViewModel, primaryColor: Color, onDismiss: () -> Unit) {
    val sources by viewModel.allRevenueSources.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var nameStr by remember { mutableStateOf("") }
    var colorStr by remember { mutableStateOf("#3B82F6") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = CardSurface) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp)) {
            Text("Quản lý Nguồn thu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.heightIn(max = 200.dp).verticalScroll(rememberScrollState())) {
                sources.forEach { source ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val c = try { Color(android.graphics.Color.parseColor(source.colorHex)) } catch (e: Exception) { Color.Gray }
                            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(c))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(source.name, fontWeight = FontWeight.Medium)
                        }
                        if (!source.isDefault) {
                            IconButton(onClick = { viewModel.deleteRevenueSource(source) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                            }
                        }
                    }
                    HorizontalDivider(color = CardBorder)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Thêm nguồn mới", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(value = nameStr, onValueChange = { nameStr = it }, label = { Text("Tên nguồn") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { 
                if (nameStr.isNotBlank()) {
                    viewModel.addRevenueSource(nameStr, colorStr)
                    nameStr = ""
                }
            }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) { Text("THÊM") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesSheet(viewModel: LedgerViewModel, primaryColor: Color, onDismiss: () -> Unit) {
    val categories by viewModel.allExpenseCategories.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var nameStr by remember { mutableStateOf("") }
    var editingCategory by remember { mutableStateOf<com.example.data.ExpenseCategory?>(null) }
    var editNameStr by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = CardSurface) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp).imePadding()) {
            Text("Quản lý Danh mục", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.heightIn(max = 280.dp).verticalScroll(rememberScrollState())) {
                categories.forEach { category ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        if (editingCategory?.id == category.id) {
                            OutlinedTextField(
                                value = editNameStr,
                                onValueChange = { editNameStr = it },
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                if (editNameStr.isNotBlank()) {
                                    viewModel.updateExpenseCategory(category.copy(name = editNameStr))
                                }
                                editingCategory = null
                            }) { Icon(Icons.Default.Check, contentDescription = "Lưu", tint = primaryColor) }
                            IconButton(onClick = { editingCategory = null }) { Icon(Icons.Default.Close, contentDescription = "Hủy", tint = Color.Gray) }
                        } else {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(category.name, fontWeight = FontWeight.Medium, color = if (category.isActive) Color(0xFF0F172A) else Color(0xFF94A3B8))
                                if (!category.isActive) {
                                    Text("Đã ẩn", style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444))
                                }
                            }
                            Row {
                                if (!category.isDefault) {
                                    IconButton(onClick = {
                                        editingCategory = category
                                        editNameStr = category.name
                                    }) { Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color(0xFF64748B)) }
                                }
                                IconButton(onClick = { 
                                    if (category.isActive) {
                                        viewModel.updateExpenseCategory(category.copy(isActive = false))
                                    } else {
                                        viewModel.updateExpenseCategory(category.copy(isActive = true))
                                    }
                                }) { 
                                    Icon(if (category.isActive) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = "Ẩn/Hiện", tint = Color(0xFF64748B)) 
                                }
                                if (!category.isDefault) {
                                    IconButton(onClick = { viewModel.deleteExpenseCategory(category) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = CardBorder)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Thêm danh mục mới", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(value = nameStr, onValueChange = { nameStr = it }, label = { Text("Tên danh mục") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { 
                if (nameStr.isNotBlank()) {
                    viewModel.addExpenseCategory(nameStr, "more_horiz")
                    nameStr = ""
                }
            }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) { Text("THÊM") }
        }
    }
}
