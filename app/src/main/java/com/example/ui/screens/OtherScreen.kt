package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodels.LedgerViewModel

@Composable
fun OtherScreen(viewModel: LedgerViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cài đặt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 32.dp, top = 16.dp))
        
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            ListItem(
                headlineContent = { Text("Quản lý nguồn thu") },
                supportingContent = { Text("Thêm, sửa, xóa các nguồn thu (Xanh SM, Grab...)") }
            )
        }
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            ListItem(
                headlineContent = { Text("Quản lý danh mục chi phí") },
                supportingContent = { Text("Thêm, sửa, xóa các loại chi phí (Sạc xe, ăn uống...)") }
            )
        }
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            ListItem(
                headlineContent = { Text("Sao lưu / Khôi phục dữ liệu") },
                supportingContent = { Text("Tính năng đang phát triển...") }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text("Made for drivers • LHN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
    }
}

