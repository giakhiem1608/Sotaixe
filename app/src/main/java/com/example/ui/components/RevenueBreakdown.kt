package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.RevenueSource
import com.example.utils.FormatUtils

@Composable
fun RevenueBreakdown(
    totalRevenue: Long,
    breakdown: List<Triple<RevenueSource, Long, Int>>,
    modifier: Modifier = Modifier
) {
    if (totalRevenue == 0L || breakdown.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Chưa có dữ liệu",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            // Stacked progress bar
            Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {
                breakdown.forEach { (source, amount, _) ->
                    val weight = amount.toFloat() / totalRevenue.toFloat()
                    val colorHex = source.colorHex.replace("#", "")
                    val color = Color(android.graphics.Color.parseColor("#$colorHex"))
                    Box(modifier = Modifier.weight(weight).fillMaxHeight().background(color))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                breakdown.forEach { (source, amount, trips) ->
                    val percent = if (totalRevenue > 0) (amount.toFloat() / totalRevenue.toFloat()) * 100 else 0f
                    val colorHex = source.colorHex.replace("#", "")
                    val color = Color(android.graphics.Color.parseColor("#$colorHex"))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(6.dp)).background(color))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(source.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                            Text("$trips cuốc • ${String.format("%.1f", percent)}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(FormatUtils.formatCurrency(amount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}
