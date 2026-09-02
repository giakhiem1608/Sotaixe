package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodels.LedgerViewModel
import com.example.utils.FormatUtils

data class ThemePreset(
    val name: String,
    val bgHex: String,
    val incomeHex: String,
    val revHex: String,
    val expHex: String
)

val PRESETS = listOf(
    ThemePreset("Mặc định", "#16A34A", "", "", ""),
    ThemePreset("Emerald", "#10B981", "#FFFFFF", "#FFFFFF", "#FCA5A5"),
    ThemePreset("Deep Navy", "#1E3A8A", "#60A5FA", "#FFFFFF", "#F87171"),
    ThemePreset("Teal", "#0F766E", "#CCFBF1", "#FFFFFF", "#FDA4AF"),
    ThemePreset("Graphite", "#374151", "#E5E7EB", "#FFFFFF", "#FCA5A5"),
    ThemePreset("Indigo", "#4338CA", "#C7D2FE", "#FFFFFF", "#FCA5A5"),
    ThemePreset("Dark Mint", "#064E3B", "#34D399", "#FFFFFF", "#F87171")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsSheet(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val currentBgHex by viewModel.cardBgColor.collectAsState()
    val currentIncomeHex by viewModel.incomeColor.collectAsState()
    val currentRevHex by viewModel.revenueColor.collectAsState()
    val currentExpHex by viewModel.expenseColor.collectAsState()
    
    var tempBgHex by remember { mutableStateOf(currentBgHex) }
    var tempIncomeHex by remember { mutableStateOf(currentIncomeHex) }
    var tempRevHex by remember { mutableStateOf(currentRevHex) }
    var tempExpHex by remember { mutableStateOf(currentExpHex) }
    
    var isCustomMode by remember { mutableStateOf(false) }

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
            Text("Tùy chỉnh Card Thu Nhập", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Preview Card
            ThemePreviewCard(
                bgHex = tempBgHex,
                incomeHex = tempIncomeHex,
                revHex = tempRevHex,
                expHex = tempExpHex
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Mẫu có sẵn", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { isCustomMode = !isCustomMode }) {
                    Text(if (isCustomMode) "Chọn mẫu" else "Tùy chỉnh")
                }
            }
            
            if (isCustomMode) {
                // Custom color inputs
                ColorInputRow("Màu nền", tempBgHex) { tempBgHex = it }
                ColorInputRow("Màu chữ Thu nhập", tempIncomeHex) { tempIncomeHex = it }
                ColorInputRow("Màu chữ Doanh thu", tempRevHex) { tempRevHex = it }
                ColorInputRow("Màu chữ Chi phí", tempExpHex) { tempExpHex = it }
                Text("Gợi ý: Bỏ trống để hệ thống tự chọn màu chữ phù hợp.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                // Preset Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(PRESETS) { preset ->
                        val isSelected = tempBgHex == preset.bgHex && tempIncomeHex == preset.incomeHex && tempRevHex == preset.revHex && tempExpHex == preset.expHex
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    tempBgHex = preset.bgHex
                                    tempIncomeHex = preset.incomeHex
                                    tempRevHex = preset.revHex
                                    tempExpHex = preset.expHex
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(try { Color(android.graphics.Color.parseColor(preset.bgHex)) } catch (e: Exception) { Color.Gray })
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.align(Alignment.Center))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(preset.name, style = MaterialTheme.typography.bodySmall, maxLines = 1, softWrap = false)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("HỦY")
                }
                Button(
                    onClick = {
                        viewModel.updateCardColors(tempBgHex, tempIncomeHex, tempRevHex, tempExpHex)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("ÁP DỤNG", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ColorInputRow(label: String, hexValue: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = hexValue,
            onValueChange = onValueChange,
            modifier = Modifier.width(120.dp),
            singleLine = true,
            placeholder = { Text("#RRGGBB") }
        )
    }
}

@Composable
fun ThemePreviewCard(bgHex: String, incomeHex: String, revHex: String, expHex: String) {
    val bgColor = try { Color(android.graphics.Color.parseColor(bgHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primaryContainer }
    val autoOnColor = if (bgColor.luminance() > 0.5f) Color.Black else Color.White
    
    val incomeColor = if (incomeHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(incomeHex)) } catch (e: Exception) { autoOnColor } else autoOnColor
    val revColor = if (revHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(revHex)) } catch (e: Exception) { autoOnColor } else autoOnColor
    val expColor = if (expHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expHex)) } catch (e: Exception) { com.example.ui.theme.ExpenseError } else com.example.ui.theme.ExpenseError

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("THU NHẬP HÔM NAY", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
            Text(
                text = "150.000 ₫",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                fontWeight = FontWeight.Bold,
                color = incomeColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Doanh thu", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
                    Text("750.000 ₫", fontWeight = FontWeight.Bold, color = revColor)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Chi phí", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
                    Text("- 600.000 ₫", fontWeight = FontWeight.Bold, color = expColor)
                }
            }
        }
    }
}
