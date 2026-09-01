package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ExpenseError
import com.example.ui.viewmodels.LedgerViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(viewModel: LedgerViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentMonth by viewModel.currentMonth.collectAsState()
    
    var showExportDialog by remember { mutableStateOf(false) }
    var showManageSources by remember { mutableStateOf(false) }
    var showManageCategories by remember { mutableStateOf(false) }
    
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog2 by remember { mutableStateOf(false) }
    
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
                        coroutineScope.launch { Toast.makeText(context, "Sao lưu thành công!", Toast.LENGTH_SHORT).show() }
                    } catch (e: Exception) {
                        coroutineScope.launch { Toast.makeText(context, "Lỗi khi ghi file", Toast.LENGTH_SHORT).show() }
                    }
                } else {
                    coroutineScope.launch { Toast.makeText(context, "Lỗi tạo bản sao lưu", Toast.LENGTH_SHORT).show() }
                }
            }
        }
    }
    
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val json = stream.bufferedReader().use { reader -> reader.readText() }
                    viewModel.restoreBackupData(json) { success ->
                        if(success) {  } else { coroutineScope.launch { Toast.makeText(context, "Lỗi khôi phục", Toast.LENGTH_SHORT).show() } }
                    }
                    
                }
            } catch (e: Exception) {
                coroutineScope.launch { Toast.makeText(context, "Lỗi đọc file", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Cài đặt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp))
        
        Text("QUẢN LÝ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
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
                    title = "Danh mục chi phí",
                    subtitle = "Thêm, sửa, ẩn loại chi phí",
                    icon = Icons.Default.Category,
                    onClick = { showManageCategories = true }
                )
            }
        }
        
        Text("DỮ LIỆU", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsMenuItem(
                    title = "Xuất báo cáo",
                    subtitle = "Excel (.xlsx)",
                    icon = Icons.Default.Download,
                    onClick = { showExportDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Sao lưu dữ liệu",
                    subtitle = "Tạo bản sao lưu an toàn",
                    icon = Icons.Default.Backup,
                    onClick = {
                        val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                        val dateString = format.format(Date())
                        backupLauncher.launch("SoTaiXe_Backup_$dateString.json")
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Khôi phục dữ liệu",
                    subtitle = "Khôi phục từ bản sao lưu",
                    icon = Icons.Default.Restore,
                    onClick = { restoreLauncher.launch(arrayOf("application/json", "*/*")) }
                )
            }
        }
        
        Text("HỆ THỐNG", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                SettingsMenuItem(
                    title = "Xóa toàn bộ dữ liệu",
                    subtitle = "Đặt lại ứng dụng như ban đầu",
                    icon = Icons.Default.DeleteForever,
                    onClick = { showResetConfirmDialog = true },
                    isDestructive = true
                )
            }
        }
    }
    
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Xóa toàn bộ dữ liệu?") },
            text = { Text("Tất cả giao dịch, nguồn thu, chi phí sẽ bị xóa vĩnh viễn. Hành động này KHÔNG THỂ hoàn tác.") },
            confirmButton = {
                TextButton(onClick = { 
                    showResetConfirmDialog = false
                    showResetConfirmDialog2 = true
                }) {
                    Text("TIẾP TỤC", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) { Text("HỦY") }
            }
        )
    }
    
    if (showResetConfirmDialog2) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog2 = false },
            title = { Text("Xác nhận lần cuối") },
            text = { Text("Bạn có thực sự chắc chắn muốn xóa toàn bộ dữ liệu không?") },
            confirmButton = {
                Button(onClick = { 
                    coroutineScope.launch { viewModel.resetAllData() }
                    showResetConfirmDialog2 = false
                    Toast.makeText(context, "Đã xóa toàn bộ dữ liệu", Toast.LENGTH_SHORT).show()
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("XÓA DỮ LIỆU")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog2 = false }) { Text("HỦY") }
            }
        )
    }
    
    if (showExportDialog) {
        ExportBottomSheet(viewModel = viewModel, onDismiss = { showExportDialog = false }, context = context)
    }
    
    if (showManageSources) {
        ManageSourcesDialog(viewModel = viewModel, onDismiss = { showManageSources = false })
    }
    
    if (showManageCategories) {
        ManageCategoriesDialog(viewModel = viewModel, onDismiss = { showManageCategories = false })
    }
}

@Composable
fun SettingsMenuItem(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit, isDestructive: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isDestructive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = if (isDestructive) ExpenseError else MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSourcesDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sources by viewModel.activeRevenueSources.collectAsState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quản lý nguồn thu") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                sources.forEach { source ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(source.name, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("ĐÓNG") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val categories by viewModel.activeExpenseCategories.collectAsState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Danh mục chi phí") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                categories.forEach { cat ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(cat.name, modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("ĐÓNG") } }
    )
}
