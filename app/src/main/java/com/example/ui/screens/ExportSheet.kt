package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(
    viewModel: LedgerViewModel,
    onDismiss: () -> Unit,
    context: android.content.Context
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var selectedOption by remember { mutableStateOf(2) } // 0: Hôm nay, 1: 7 ngày, 2: Tháng này, 3: Tháng trước, 4: Tùy chỉnh
    
    val cal = Calendar.getInstance()
    var toDate by remember { mutableStateOf(cal.timeInMillis) }
    var fromDate by remember { mutableStateOf(run { cal.set(Calendar.DAY_OF_MONTH, 1); cal.timeInMillis }) }
    
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }
    
    // Update dates based on selected option
    LaunchedEffect(selectedOption) {
        val c = Calendar.getInstance()
        when (selectedOption) {
            0 -> { // Hôm nay
                fromDate = c.timeInMillis
                toDate = c.timeInMillis
            }
            1 -> { // 7 ngày
                toDate = c.timeInMillis
                c.add(Calendar.DAY_OF_YEAR, -6)
                fromDate = c.timeInMillis
            }
            2 -> { // Tháng này
                toDate = c.timeInMillis
                c.set(Calendar.DAY_OF_MONTH, 1)
                fromDate = c.timeInMillis
            }
            3 -> { // Tháng trước
                c.set(Calendar.DAY_OF_MONTH, 1)
                c.add(Calendar.DAY_OF_YEAR, -1)
                toDate = c.timeInMillis
                c.set(Calendar.DAY_OF_MONTH, 1)
                fromDate = c.timeInMillis
            }
        }
    }
    
    val fromDateStr = FormatUtils.formatDate(fromDate)
    val toDateStr = FormatUtils.formatDate(toDate)
    
    val dbFromDateStr = FormatUtils.formatDbDate(fromDate)
    val dbToDateStr = FormatUtils.formatDbDate(toDate)
    
    val label = when (selectedOption) {
        0 -> "Hôm nay ($fromDateStr)"
        1 -> "7 ngày qua ($fromDateStr - $toDateStr)"
        2 -> "Tháng này ($fromDateStr - $toDateStr)"
        3 -> "Tháng trước ($fromDateStr - $toDateStr)"
        else -> "Tùy chỉnh ($fromDateStr - $toDateStr)"
    }
    
    val fileName = if (fromDateStr == toDateStr) {
        "SoTaiXe_$dbFromDateStr.xlsx"
    } else {
        "SoTaiXe_${dbFromDateStr}_${dbToDateStr}.xlsx"
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) { uri: Uri? ->
        uri?.let {
            val outputStream = context.contentResolver.openOutputStream(it)
            if (outputStream != null) {
                viewModel.exportXlsxDataRange(dbFromDateStr, dbToDateStr, label, outputStream) { success ->
                    if (success) {
                        android.widget.Toast.makeText(context, "Xuất Excel thành công!", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(context, "Lỗi khi xuất file", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    onDismiss()
                }
            } else {
                onDismiss()
            }
        } ?: run {
            onDismiss()
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("XUẤT DỮ LIỆU", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Khoảng thời gian:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            val options = listOf("Hôm nay", "7 ngày gần nhất", "Tháng này", "Tháng trước", "Tùy chỉnh")
            options.forEachIndexed { index, text ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { selectedOption = index }.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedOption == index, onClick = { selectedOption = index })
                    Text(text)
                }
            }
            
            if (selectedOption == 4) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = { showFromDatePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(fromDateStr)
                    }
                    OutlinedButton(onClick = { showToDatePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(toDateStr)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Khoảng thời gian:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$fromDateStr – $toDateStr", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val isCustomValid = fromDate <= toDate
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("HỦY", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = { launcher.launch(fileName) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = isCustomValid
                ) {
                    Text("XUẤT EXCEL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    
    if (showFromDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = fromDate)
        DatePickerDialog(
            onDismissRequest = { showFromDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { fromDate = it }
                    showFromDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showFromDatePicker = false }) { Text("HỦY") }
            }
        ) {
            DatePicker(state = state)
        }
    }
    
    if (showToDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = toDate)
        DatePickerDialog(
            onDismissRequest = { showToDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { toDate = it }
                    showToDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showToDatePicker = false }) { Text("HỦY") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}
