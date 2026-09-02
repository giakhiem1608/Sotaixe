package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val themeColors = listOf(
    "#16A34A", // Xanh ngọc (Emerald)
    "#0F172A", // Xanh navy (Slate 900)
    "#2563EB", // Xanh dương (Blue)
    "#7C3AED", // Tím (Violet)
    "#EA580C", // Cam (Orange)
    "#B45309", // Vàng đậm (Amber)
    "#9F1239", // Đỏ rượu (Rose)
    "#0D9488", // Teal
    "#0284C7", // Light Blue
    "#4F46E5", // Indigo
    "#C026D3", // Fuchsia
    "#475569"  // Blue Gray
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeColorPickerSheet(
    currentColor: String,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                "Chọn màu doanh thu",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (col in 0..3) {
                            val index = row * 4 + col
                            if (index < themeColors.size) {
                                val colorHex = themeColors[index]
                                ThemeColorDot(
                                    colorHex = colorHex,
                                    isSelected = currentColor == colorHex,
                                    onClick = {
                                        onColorSelected(colorHex)
                                        onDismiss()
                                    }
                                )
                            } else {
                                Spacer(modifier = Modifier.size(48.dp))
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { 
                    onColorSelected("#16A34A") // Default
                    onDismiss()
                }) {
                    Text("Khôi phục mặc định")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Hủy")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ThemeColorDot(colorHex: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Color.Gray }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(Icons.Filled.Check, contentDescription = "Selected", tint = Color.White)
        }
    }
}
