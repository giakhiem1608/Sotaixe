import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

# I will replace from // Stats Card to // Sources Breakdown

new_stats_card = """                // Stats Card
                item {
                    var showAdvancedStats by remember { mutableStateOf(false) }
                    Text("Thống kê", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("TỔNG QUAN", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            StatRow("Số ngày chạy", "$daysWorked ngày")
                            StatRow("Tổng số cuốc", "$totalTrips cuốc")
                            
                            val totalKm = revenueEntries.sumOf { (it.distanceKm ?: 0f).toDouble() }.toFloat()
                            val totalHours = revenueEntries.sumOf { (it.durationHrs ?: 0f).toDouble() }.toFloat()
                            
                            if (totalKm > 0) {
                                StatRow("Tổng km", "${String.format("%.1f", totalKm).replace(".", ",")} km")
                            }
                            if (totalHours > 0) {
                                StatRow("Tổng giờ chạy", "${String.format("%.1f", totalHours).replace(".", ",")} giờ")
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("HIỆU QUẢ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val avgNetPerDay = if (daysWorked > 0) netIncome / daysWorked else 0L
                            StatRow("TB Thu nhập/ngày", FormatUtils.formatCurrency(avgNetPerDay))
                            
                            val avgPerTrip = if (totalTrips > 0) totalRev / totalTrips else 0L
                            StatRow("TB Doanh thu/cuốc", FormatUtils.formatCurrency(avgPerTrip))
                            
                            val revWithKm = revenueEntries.filter { (it.distanceKm ?: 0f) > 0f }.sumOf { it.amount }
                            val revPerKm = if (totalKm > 0) (revWithKm / totalKm).toLong() else 0L
                            
                            if (totalKm > 0) {
                                StatRow("Doanh thu/km", FormatUtils.formatCurrency(revPerKm))
                            }
                            
                            if (showAdvancedStats) {
                                // Tỉ lệ chi phí so với doanh thu trung bình để tính thu nhập trên km
                                val ratioNetToRev = if (totalRev > 0) netIncome.toDouble() / totalRev.toDouble() else 0.0
                                val netPerKm = (revPerKm * ratioNetToRev).toLong()
                                
                                if (totalKm > 0) {
                                    StatRow("Thu nhập/km", FormatUtils.formatCurrency(netPerKm))
                                }
                                
                                val revWithHours = revenueEntries.filter { (it.durationHrs ?: 0f) > 0f }.sumOf { it.amount }
                                val revPerHour = if (totalHours > 0) (revWithHours / totalHours).toLong() else 0L
                                val netPerHour = (revPerHour * ratioNetToRev).toLong()
                                
                                if (totalHours > 0) {
                                    StatRow("Doanh thu/giờ", FormatUtils.formatCurrency(revPerHour))
                                    StatRow("Thu nhập/giờ", FormatUtils.formatCurrency(netPerHour))
                                }
                            }
                            
                            TextButton(
                                onClick = { showAdvancedStats = !showAdvancedStats },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Text(if (showAdvancedStats) "Thu gọn" else "Xem thêm chỉ số")
                            }
                        }
                    }
                }
                
                // Efficiency Breakdown
                item {
                    Text("Hiệu quả theo nguồn thu", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val activeSources = sources.filter { s -> revenueEntries.any { it.sourceId == s.id } }
                        activeSources.forEach { source ->
                            val srcEntries = revenueEntries.filter { it.sourceId == source.id }
                            val srcTrips = srcEntries.sumOf { it.trips }
                            val srcRev = srcEntries.sumOf { it.amount }
                            val srcKm = srcEntries.sumOf { (it.distanceKm ?: 0f).toDouble() }.toFloat()
                            val srcRevWithKm = srcEntries.filter { (it.distanceKm ?: 0f) > 0f }.sumOf { it.amount }
                            
                            val srcAvgTrip = if (srcTrips > 0) srcRev / srcTrips else 0L
                            val srcRevPerKm = if (srcKm > 0) (srcRevWithKm / srcKm).toLong() else 0L
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(12.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Color(android.graphics.Color.parseColor(source.colorHex))))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(source.name, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(horizontalAlignment = Alignment.Start) {
                                            Text("$srcTrips cuốc", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(FormatUtils.formatCurrency(srcRev), fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("TB/cuốc", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(FormatUtils.formatCurrency(srcAvgTrip), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (srcKm > 0) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("TB/km", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(FormatUtils.formatCurrency(srcRevPerKm), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Sources Breakdown"""

match_str = r"// Stats Card.*?// Sources Breakdown"
content = re.sub(match_str, new_stats_card, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)
