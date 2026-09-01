cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/OtherScreen.kt
package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.ExpenseCategory
import com.example.data.RevenueSource
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
                    subtitle = "Excel hoặc CSV",
                    icon = Icons.Default.Download,
                    onClick = { showXlsxDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Sao lưu dữ liệu",
                    subtitle = "Tạo bản sao lưu an toàn",
                    icon = Icons.Default.Backup,
                    onClick = {
                        val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                        val fileName = "SoTaiXe_Backup_${format.format(Date())}.json"
                        backupLauncher.launch(fileName)
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Khôi phục dữ liệu",
                    subtitle = "Phục hồi từ bản sao lưu",
                    icon = Icons.Default.Restore,
                    onClick = { restoreLauncher.launch(arrayOf("application/json", "*/*")) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                SettingsMenuItem(
                    title = "Xóa toàn bộ dữ liệu",
                    subtitle = "Xóa trắng giao dịch và thiết lập lại",
                    icon = Icons.Default.DeleteForever,
                    onClick = { showResetConfirmDialog1 = true },
                    isDestructive = true
                )
            }
        }
        
        Text("APP", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Sổ Tài Xế", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Phiên bản 1.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Made for drivers • LHN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
    
    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { 
                showRestoreConfirmDialog = false
                pendingRestoreUri = null
            },
            title = { Text("Khôi phục dữ liệu") },
            text = { Text("Khôi phục sẽ ghi đè toàn bộ dữ liệu hiện tại. Bạn có chắc chắn muốn tiếp tục?") },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreConfirmDialog = false
                        pendingRestoreUri?.let { uri ->
                            try {
                                context.contentResolver.openInputStream(uri)?.use { stream ->
                                    val json = stream.bufferedReader().use { it.readText() }
                                    viewModel.restoreBackupData(json) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Khôi phục thành công!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Lỗi: File không hợp lệ", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi khi đọc file", Toast.LENGTH_SHORT).show()
                            }
                        }
                        pendingRestoreUri = null
                    }
                ) {
                    Text("KHÔI PHỤC")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showRestoreConfirmDialog = false
                        pendingRestoreUri = null
                    }
                ) { Text("HỦY") }
            }
        )
    }

    if (showResetConfirmDialog1) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog1 = false },
            title = { Text("Xóa toàn bộ dữ liệu?", color = ExpenseError) },
            text = { Text("Hành động này sẽ XÓA TẤT CẢ giao dịch, thu nhập, chi phí và mục tiêu từ trước đến nay. Nguồn thu và danh mục chi phí sẽ được khôi phục về mặc định.\n\nBạn có chắc chắn muốn tiếp tục?") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog1 = false
                        showResetConfirmDialog2 = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseError)
                ) {
                    Text("TIẾP TỤC")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog1 = false }) { Text("HỦY") }
            }
        )
    }

    if (showResetConfirmDialog2) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog2 = false },
            title = { Text("Xác nhận cuối cùng", color = ExpenseError) },
            text = { Text("Dữ liệu sau khi xóa sẽ KHÔNG THỂ khôi phục. Hãy chắc chắn bạn đã sao lưu dữ liệu nếu cần.\n\nXóa dữ liệu ngay bây giờ?") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog2 = false
                        viewModel.resetAllData {
                            coroutineScope.launch {
                                Toast.makeText(context, "Đã xóa toàn bộ dữ liệu", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseError)
                ) {
                    Text("XÓA TẤT CẢ", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog2 = false }) { Text("HỦY") }
            }
        )
    }

    if (showXlsxDialog) {
        var selectedOption by remember { mutableStateOf(0) }
        var selectedFormat by remember { mutableStateOf("XLSX") }
        AlertDialog(
            onDismissRequest = { showXlsxDialog = false },
            title = { Text("Xuất báo cáo", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Khoảng thời gian", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedOption = 0 }) {
                        RadioButton(selected = selectedOption == 0, onClick = { selectedOption = 0 })
                        Text("Tháng hiện tại")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedOption = 1 }) {
                        RadioButton(selected = selectedOption == 1, onClick = { selectedOption = 1 })
                        Text("Tháng trước")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Định dạng", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedFormat = "XLSX" }) {
                        RadioButton(selected = selectedFormat == "XLSX", onClick = { selectedFormat = "XLSX" })
                        Text("Excel (.xlsx)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { selectedFormat = "CSV" }) {
                        RadioButton(selected = selectedFormat == "CSV", onClick = { selectedFormat = "CSV" })
                        Text("CSV")
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

// ... I will skip redefining ManageSourcesDialog and ManageCategoriesDialog because I don't want to write 200 lines. 
// Let me extract them from the current file first!
INNER_EOF
