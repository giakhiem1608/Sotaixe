content = """package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodels.APP_PRESETS
import com.example.ui.viewmodels.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsSheet(
    themeManager: ThemeManager,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val activePreset by themeManager.activePreset.collectAsState()
    val primaryHex by themeManager.primaryColorHex.collectAsState()
    val heroBgHex by themeManager.heroBgColorHex.collectAsState()
    val recentColors by themeManager.recentColors.collectAsState()

    var showCustomPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss, 
        sheetState = sheetState,
        containerColor = CardSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("CHỦ ĐỀ & MÀU SẮC", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))

            // LIVE PREVIEW
            HeroIncomeCard(
                netIncome = 1250000,
                revenue = 1500000,
                tip = 50000,
                expense = 300000,
                trips = 15,
                distance = 103.5f,
                bgHex = heroBgHex
            )

            // PRESETS
            Column {
                Text("MẪU GỢI Ý", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(16.dp))
                
                APP_PRESETS.chunked(2).forEach { rowPresets ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowPresets.forEach { preset ->
                            val color = try { Color(android.graphics.Color.parseColor(preset.heroBgHex)) } catch(e: Exception) { Color.Gray }
                            val isSelected = activePreset.id == preset.id
                            
                            Surface(
                                modifier = Modifier.weight(1f).height(64.dp).clickable {
                                    themeManager.setPreset(preset.id)
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = color,
                                border = if (isSelected) BorderStroke(3.dp, Color(0xFF0F172A)) else null
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        preset.name, 
                                        fontWeight = FontWeight.Bold,
                                        color = if (androidx.compose.ui.graphics.luminance(color) > 0.5f) Color(0xFF0F172A) else Color.White
                                    )
                                }
                            }
                        }
                        if (rowPresets.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Custom button
                Surface(
                    modifier = Modifier.fillMaxWidth().height(64.dp).clickable { showCustomPicker = !showCustomPicker },
                    shape = RoundedCornerShape(16.dp),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (showCustomPicker) "Thu gọn" else "Tùy chỉnh màu nâng cao", fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                    }
                }
            }
            
            if (showCustomPicker) {
                CustomColorPicker(
                    recentColors = recentColors,
                    onColorSelected = { hex ->
                        themeManager.setCustomColors(hex, hex)
                    }
                )
            }
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = try { Color(android.graphics.Color.parseColor(primaryHex)) } catch(e: Exception) { Color(0xFF0284C7) })
            ) {
                Text("XONG", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CustomColorPicker(
    recentColors: List<String>,
    onColorSelected: (String) -> Unit
) {
    val curatedColors = listOf(
        "#0284C7", "#0369A1", "#075985", // Blue
        "#10B981", "#047857", "#064E3B", // Emerald
        "#0D9488", "#0F766E", "#115E59", // Teal
        "#8B5CF6", "#6D28D9", "#4C1D95", // Purple
        "#F59E0B", "#D97706", "#B45309", // Amber
        "#EF4444", "#B91C1C", "#7F1D1D", // Red
        "#F43F5E", "#E11D48", "#9F1239", // Rose
        "#64748B", "#475569", "#1E293B"  // Slate
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("TÙY CHỈNH NÂNG CAO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))
        
        if (recentColors.isNotEmpty()) {
            Text("Gần đây", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                recentColors.take(6).forEach { hex ->
                    val color = try { Color(android.graphics.Color.parseColor(hex)) } catch(e: Exception) { Color.Gray }
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color).clickable { onColorSelected(hex) })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Text("Bảng màu", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(8.dp))
        
        curatedColors.chunked(6).forEach { rowColors ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                rowColors.forEach { hex ->
                    val color = try { Color(android.graphics.Color.parseColor(hex)) } catch(e: Exception) { Color.Gray }
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(color).clickable { onColorSelected(hex) })
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "w") as f:
    f.write(content)
