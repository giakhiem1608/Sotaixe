package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodels.LedgerViewModel

@Composable
fun OtherScreen(viewModel: LedgerViewModel) {
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showManageSources by remember { mutableStateOf(false) }
    var showManageCategories by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cài đặt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 32.dp, top = 16.dp))
        
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { showManageSources = true }) {
            ListItem(
                headlineContent = { Text("Quản lý nguồn thu") },
                supportingContent = { Text("Thêm, xóa các nguồn thu") }
            )
        }
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { showManageCategories = true }) {
            ListItem(
                headlineContent = { Text("Quản lý danh mục chi phí") },
                supportingContent = { Text("Thêm, xóa các loại chi phí") }
            )
        }
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { showBackupDialog = true }) {
            ListItem(
                headlineContent = { Text("Sao lưu dữ liệu") },
                supportingContent = { Text("Tạo mã sao lưu toàn bộ dữ liệu") }
            )
        }
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { showRestoreDialog = true }) {
            ListItem(
                headlineContent = { Text("Khôi phục dữ liệu") },
                supportingContent = { Text("Khôi phục từ mã sao lưu (Sẽ xóa dữ liệu hiện tại)") }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text("Made for drivers • LHN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
    }

    if (showBackupDialog) {
        BackupDialog(viewModel = viewModel, onDismiss = { showBackupDialog = false })
    }

    if (showRestoreDialog) {
        RestoreDialog(viewModel = viewModel, onDismiss = { showRestoreDialog = false })
    }

    if (showManageSources) {
        ManageSourcesDialog(viewModel = viewModel, onDismiss = { showManageSources = false })
    }

    if (showManageCategories) {
        ManageCategoriesDialog(viewModel = viewModel, onDismiss = { showManageCategories = false })
    }
}

@Composable
fun ManageSourcesDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sources by viewModel.activeRevenueSources.collectAsState()
    var newName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quản lý nguồn thu") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(sources) { source ->
                        ListItem(
                            headlineContent = { Text(source.name) },
                            trailingContent = {
                                IconButton(onClick = { viewModel.hideRevenueSource(source) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Tên nguồn thu mới") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addRevenueSource(newName, "#2979FF") // Default to blue
                    newName = ""
                },
                enabled = newName.isNotBlank()
            ) {
                Text("THÊM")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ĐÓNG") }
        }
    )
}

@Composable
fun ManageCategoriesDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val categories by viewModel.activeExpenseCategories.collectAsState()
    var newName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quản lý danh mục") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(categories) { category ->
                        ListItem(
                            headlineContent = { Text(category.name) },
                            trailingContent = {
                                IconButton(onClick = { viewModel.hideExpenseCategory(category) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Tên danh mục mới") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.addExpenseCategory(newName, "more_horiz")
                    newName = ""
                },
                enabled = newName.isNotBlank()
            ) {
                Text("THÊM")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ĐÓNG") }
        }
    )
}

@Composable
fun BackupDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    var backupString by remember { mutableStateOf("Đang tạo mã...") }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.generateBackupData { json ->
            if (json != null) {
                backupString = json
            } else {
                backupString = "Lỗi khi tạo mã sao lưu."
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mã sao lưu của bạn") },
        text = {
            OutlinedTextField(
                value = backupString,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        },
        confirmButton = {
            Button(onClick = {
                clipboardManager.setText(AnnotatedString(backupString))
                Toast.makeText(context, "Đã chép vào bộ nhớ tạm", Toast.LENGTH_SHORT).show()
                onDismiss()
            }) {
                Text("SAO CHÉP")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ĐÓNG")
            }
        }
    )
}

@Composable
fun RestoreDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    var jsonString by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Khôi phục dữ liệu") },
        text = {
            Column {
                Text("CẢNH BÁO: Dữ liệu hiện tại sẽ bị xóa và thay thế bằng dữ liệu từ mã khôi phục.", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = jsonString,
                    onValueChange = { jsonString = it },
                    label = { Text("Dán mã khôi phục vào đây") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.restoreBackupData(jsonString) { success ->
                        if (success) {
                            Toast.makeText(context, "Khôi phục thành công", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Lỗi: Mã khôi phục không hợp lệ", Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                    }
                },
                enabled = jsonString.isNotBlank()
            ) {
                Text("KHÔI PHỤC")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("HỦY")
            }
        }
    )
}

