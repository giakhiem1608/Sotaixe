package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.viewmodels.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsSheet(
    themeManager: ThemeManager,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val currentBgHex by themeManager.cardBgColor.collectAsState()
    val currentIncomeHex by themeManager.incomeColor.collectAsState()
    val currentRevHex by themeManager.revenueColor.collectAsState()
    val currentExpHex by themeManager.expenseColor.collectAsState()
    val currentTipHex by themeManager.tipColor.collectAsState()

    var tempBgHex by remember { mutableStateOf(currentBgHex) }
    var tempIncomeHex by remember { mutableStateOf(currentIncomeHex) }
    var tempRevHex by remember { mutableStateOf(currentRevHex) }
    var tempExpHex by remember { mutableStateOf(currentExpHex) }
    var tempTipHex by remember { mutableStateOf(currentTipHex) }

    var isCustomMode by remember { mutableStateOf(false) }

    var showColorPicker by remember { mutableStateOf(false) }
    var colorPickerTarget by remember { mutableStateOf("") }
    var colorPickerCurrentHex by remember { mutableStateOf("") }

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
            Text("TÙY CHỈNH CARD THU NHẬP", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Preview Card
            ThemePreviewCard(
                bgHex = tempBgHex,
                incomeHex = tempIncomeHex,
                revHex = tempRevHex,
                expHex = tempExpHex,
                tipHex = tempTipHex
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Mẫu có sẵn", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Row {
                    TextButton(onClick = { 
                        tempBgHex = "#111827"
                        tempIncomeHex = "#FFFFFF"
                        tempRevHex = "#22C55E"
                        tempExpHex = "#F05D5E"
                        tempTipHex = "#F59E0B"
                    }) {
                        Text("Reset", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = { isCustomMode = !isCustomMode }) {
                        Text(if (isCustomMode) "Chọn mẫu >" else "Tùy chỉnh >")
                    }
                }
            }
            
            if (isCustomMode) {
                Text("Màu sắc", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                Text("Chạm vào màu để thay đổi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp))
                
                ColorSelectRow("Màu nền", tempBgHex) { 
                    colorPickerTarget = "bg"
                    colorPickerCurrentHex = tempBgHex
                    showColorPicker = true 
                }
                ColorSelectRow("Thu nhập", tempIncomeHex, allowAuto = true) { 
                    colorPickerTarget = "income"
                    colorPickerCurrentHex = tempIncomeHex
                    showColorPicker = true 
                }
                ColorSelectRow("Doanh thu", tempRevHex, allowAuto = true) { 
                    colorPickerTarget = "rev"
                    colorPickerCurrentHex = tempRevHex
                    showColorPicker = true 
                }
                ColorSelectRow("Chi phí", tempExpHex, allowAuto = true) { 
                    colorPickerTarget = "exp"
                    colorPickerCurrentHex = tempExpHex
                    showColorPicker = true 
                }
                ColorSelectRow("Tip", tempTipHex, allowAuto = true) { 
                    colorPickerTarget = "tip"
                    colorPickerCurrentHex = tempTipHex
                    showColorPicker = true 
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                // Theme Presets
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ThemePresetBox("Midnight Finance", "#111827", "#FFFFFF", "#22C55E", "#F05D5E", "#F59E0B") { bg, inc, rev, exp, tip ->
                        tempBgHex = bg; tempIncomeHex = inc; tempRevHex = rev; tempExpHex = exp; tempTipHex = tip
                    }
                    ThemePresetBox("Classic Green", "#16A34A", "#FFFFFF", "#FFFFFF", "#FFCDD2", "#FFE082") { bg, inc, rev, exp, tip ->
                        tempBgHex = bg; tempIncomeHex = inc; tempRevHex = rev; tempExpHex = exp; tempTipHex = tip
                    }
                    ThemePresetBox("Royal Purple", "#4C1D95", "#FFFFFF", "#A78BFA", "#F87171", "#FDE047") { bg, inc, rev, exp, tip ->
                        tempBgHex = bg; tempIncomeHex = inc; tempRevHex = rev; tempExpHex = exp; tempTipHex = tip
                    }
                    ThemePresetBox("Ocean Blue", "#0369A1", "#FFFFFF", "#7DD3FC", "#FDA4AF", "#FEF08A") { bg, inc, rev, exp, tip ->
                        tempBgHex = bg; tempIncomeHex = inc; tempRevHex = rev; tempExpHex = exp; tempTipHex = tip
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    themeManager.setCardColors(tempBgHex, tempIncomeHex, tempRevHex, tempExpHex, tempTipHex)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("LƯU THAY ĐỔI")
            }
        }
    }
    
    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text(
                when (colorPickerTarget) {
                    "bg" -> "Chọn màu nền"
                    "income" -> "Chọn màu Thu nhập"
                    "rev" -> "Chọn màu Doanh thu"
                    "exp" -> "Chọn màu Chi phí"
                    "tip" -> "Chọn màu Tip"
                    else -> "Chọn màu"
                }
            ) },
            text = {
                Column {
                    if (colorPickerTarget != "bg") {
                        TextButton(
                            onClick = {
                                val autoHex = ""
                                when (colorPickerTarget) {
                                    "income" -> tempIncomeHex = autoHex
                                    "rev" -> tempRevHex = autoHex
                                    "exp" -> tempExpHex = autoHex
                                    "tip" -> tempTipHex = autoHex
                                }
                                showColorPicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tự động (theo màu chữ của nền)")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    val presetColors = listOf(
                        "#111827", "#16A34A", "#22C55E", "#3B82F6", 
                        "#8B5CF6", "#F05D5E", "#F59E0B", "#FFFFFF",
                        "#4C1D95", "#0369A1", "#0F172A", "#64748B"
                    )
                    @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        presetColors.forEach { hex ->
                            val color = try { Color(android.graphics.Color.parseColor(hex)) } catch(e: Exception) { Color.Transparent }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable {
                                        when (colorPickerTarget) {
                                            "bg" -> tempBgHex = hex
                                            "income" -> tempIncomeHex = hex
                                            "rev" -> tempRevHex = hex
                                            "exp" -> tempExpHex = hex
                                            "tip" -> tempTipHex = hex
                                        }
                                        showColorPicker = false
                                    }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPicker = false }) { Text("ĐÓNG") }
            }
        )
    }
}

@Composable
fun ThemePresetBox(
    name: String,
    bgHex: String,
    incHex: String,
    revHex: String,
    expHex: String,
    tipHex: String,
    onClick: (String, String, String, String, String) -> Unit
) {
    val bgColor = try { Color(android.graphics.Color.parseColor(bgHex)) } catch (e: Exception) { Color.Gray }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick(bgHex, incHex, revHex, expHex, tipHex) }.padding(4.dp)) {
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
            // inner preview dots
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(16.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(try { Color(android.graphics.Color.parseColor(incHex.ifEmpty { "#FFFFFF" })) } catch (e: Exception) { Color.White }))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(12.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(try { Color(android.graphics.Color.parseColor(revHex.ifEmpty { "#FFFFFF" })) } catch (e: Exception) { Color.White }))
                    Box(modifier = Modifier.size(12.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(try { Color(android.graphics.Color.parseColor(expHex.ifEmpty { "#FFFFFF" })) } catch (e: Exception) { Color.White }))
                }
            }
        }
    }
}

@Composable
fun ColorSelectRow(label: String, hexColor: String, allowAuto: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        if (hexColor.isEmpty() && allowAuto) {
            Text("Tự động", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            val color = try { Color(android.graphics.Color.parseColor(hexColor)) } catch (e: Exception) { Color.Transparent }
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color))
        }
    }
}

@Composable
fun ThemePreviewCard(bgHex: String, incomeHex: String, revHex: String, expHex: String, tipHex: String) {
    val bgColor = try { Color(android.graphics.Color.parseColor(bgHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primaryContainer }
    val autoOnColor = if (bgColor.luminance() > 0.5f) Color.Black else Color.White
    
    val incomeColor = if (incomeHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(incomeHex)) } catch (e: Exception) { autoOnColor } else autoOnColor
    val revColor = if (revHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(revHex)) } catch (e: Exception) { autoOnColor } else autoOnColor
    val expColor = if (expHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expHex)) } catch (e: Exception) { Color(0xFFF05D5E) } else Color(0xFFF05D5E)
    val tipColor = if (tipHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(tipHex)) } catch (e: Exception) { Color(0xFFF59E0B) } else Color(0xFFF59E0B)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("THU NHẬP HÔM NAY", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
            Text(
                text = "170.000 đ",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                fontWeight = FontWeight.Bold,
                color = incomeColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Doanh thu", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
                    Text("750.000 đ", fontWeight = FontWeight.Bold, color = revColor)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Chi phí", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
                    Text("- 600.000 đ", fontWeight = FontWeight.Bold, color = expColor)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Tip", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
                    Text("+ 20.000 đ", fontWeight = FontWeight.Bold, color = tipColor)
                }
            }
        }
    }
}
