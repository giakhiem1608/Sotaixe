import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

# 1. Update the Goal Card
goal_target = r"""                // Goals Card\n                item \{\n                    val goal by viewModel\.currentMonthGoal\.collectAsState\(\)\n                    var showGoalDialog by remember \{ mutableStateOf\(false\) \}\n.*?(?=                    // Main Income Card)"""
goal_replacement = """                // Goals Card
                item {
                    val goal by viewModel.currentMonthGoal.collectAsState()
                    var showGoalDialog by remember { mutableStateOf(false) }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { showGoalDialog = true },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (goal == null) {
                                Text("Chưa thiết lập mục tiêu tháng", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { showGoalDialog = true }) {
                                    Text("Thiết lập")
                                }
                            } else {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Mục tiêu tháng ${displayMonth}", fontWeight = FontWeight.Bold)
                                    Icon(Icons.Filled.Edit, contentDescription = "Sửa mục tiêu", modifier = Modifier.size(20.dp))
                                }
                                
                                val currentAmount = if (goal!!.type == "REVENUE") totalRev else netIncome
                                val percent = if (goal!!.amount > 0) (currentAmount.toFloat() / goal!!.amount.toFloat()) else 0f
                                val remain = goal!!.amount - currentAmount
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "${FormatUtils.formatCurrency(currentAmount)} / ${FormatUtils.formatCurrency(goal!!.amount)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        String.format(java.util.Locale.getDefault(), "%.1f%%", percent * 100),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                                ) {
                                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(percent.coerceIn(0f, 1f)).background(cardBgColor))
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                if (remain > 0) {
                                    Text(
                                        text = "Còn thiếu ${FormatUtils.formatCurrency(remain)} để đạt mục tiêu",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                } else {
                                    Text(
                                        text = "✓ Đã đạt mục tiêu tháng",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (remain < 0) {
                                        Text(
                                            text = "Vượt mục tiêu ${FormatUtils.formatCurrency(-remain)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    if (showGoalDialog) {
                        GoalDialog(viewModel = viewModel, onDismiss = { showGoalDialog = false })
                    }
                }
"""
content = re.sub(goal_target, goal_replacement, content, flags=re.DOTALL)


# 2. Add Missing KM Warning
# Find "// Goals Card" and prepend warning if missing KM
warning_insert = """
                val missingKmEntries = revenueEntries.filter { it.distanceKm == null || it.distanceKm <= 0f }
                val missingKmTrips = missingKmEntries.sumOf { it.trips }
                if (missingKmTrips > 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WarningAmber, contentDescription = "Cảnh báo", tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Thiếu dữ liệu quãng đường", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("$missingKmTrips/$totalTrips cuốc chưa có KM.\\nCác chỉ số liên quan KM có thể chưa chính xác.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                Spacer(modifier = Modifier.height(12.dp))
                                TextButton(
                                    onClick = { /* TODO: Navigation to history missing KM */ },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("XEM CÁC CUỐC THIẾU KM", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                
                // Goals Card"""
content = content.replace("                // Goals Card", warning_insert)

# 3. Fix KM KPIs in TỔNG QUAN and HIỆU QUẢ
stats_target = r"""                            val totalKm = revenueEntries\.sumOf \{ \(it\.distanceKm \?\: 0f\)\.toDouble\(\) \}\.toFloat\(\)\n                            val totalHours = revenueEntries\.sumOf \{ \(it\.durationHrs \?\: 0f\)\.toDouble\(\) \}\.toFloat\(\)\n                            \n                            if \(totalKm > 0\) \{\n                                StatRow\("Tổng km", "\$\{String\.format\("%.1f", totalKm\)\.replace\("\.", ","\)\} km"\)\n                            \}\n                            if \(totalHours > 0\) \{\n                                StatRow\("Tổng giờ chạy", "\$\{String\.format\("%.1f", totalHours\)\.replace\("\.", ","\)\} giờ"\)\n                            \}\n                            \n                            Spacer\(modifier = Modifier\.height\(16\.dp\)\)\n                            Text\("HIỆU QUẢ", style = MaterialTheme\.typography\.labelMedium, color = MaterialTheme\.colorScheme\.primary\)\n                            Spacer\(modifier = Modifier\.height\(8\.dp\)\)\n                            \n                            val avgNetPerDay = if \(daysWorked > 0\) netIncome / daysWorked else 0L\n                            StatRow\("TB Thu nhập/ngày", FormatUtils\.formatCurrency\(avgNetPerDay\)\)\n                            \n                            val avgRevPerDay = if \(daysWorked > 0\) totalRev / daysWorked else 0L\n                            StatRow\("TB Doanh thu/ngày", FormatUtils\.formatCurrency\(avgRevPerDay\)\)\n                            \n                            val avgRevPerTrip = if \(totalTrips > 0\) totalRev / totalTrips else 0L\n                            StatRow\("TB Doanh thu/cuốc", FormatUtils\.formatCurrency\(avgRevPerTrip\)\)\n                            \n                            if \(totalKm > 0\) \{\n                                val avgRevPerKm = totalRev / totalKm\.toLong\(\)\n                                StatRow\("Doanh thu/km", "\$\{FormatUtils\.formatCurrency\(avgRevPerKm\)\}/km"\)\n                            \}\n                            if \(totalHours > 0\) \{\n                                val avgRevPerHr = totalRev / totalHours\.toLong\(\)\n                                StatRow\("Doanh thu/giờ", "\$\{FormatUtils\.formatCurrency\(avgRevPerHr\)\}/giờ"\)\n                            \}"""

stats_replacement = """                            val validKmEntries = revenueEntries.filter { it.distanceKm != null && it.distanceKm > 0f }
                            val tripsWithKm = validKmEntries.sumOf { it.trips }
                            val totalKm = validKmEntries.sumOf { (it.distanceKm ?: 0f).toDouble() }.toFloat()
                            val totalHours = revenueEntries.sumOf { (it.durationHrs ?: 0f).toDouble() }.toFloat()
                            val kmCoverage = if (totalTrips > 0) (tripsWithKm.toFloat() / totalTrips * 100) else 0f
                            
                            if (totalKm > 0) {
                                StatRow("Tổng km", "${String.format("%.1f", totalKm).replace(".", ",")} km")
                            }
                            if (totalHours > 0) {
                                StatRow("Tổng giờ chạy", "${String.format("%.1f", totalHours).replace(".", ",")} giờ")
                            }
                            StatRow("Độ phủ KM", "${String.format("%.1f", kmCoverage).replace(".", ",")}%")
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("HIỆU QUẢ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val avgNetPerDay = if (daysWorked > 0) netIncome / daysWorked else 0L
                            StatRow("TB Thu nhập/ngày", FormatUtils.formatCurrency(avgNetPerDay))
                            
                            val avgRevPerDay = if (daysWorked > 0) totalRev / daysWorked else 0L
                            StatRow("TB Doanh thu/ngày", FormatUtils.formatCurrency(avgRevPerDay))
                            
                            val avgRevPerTrip = if (totalTrips > 0) totalRev / totalTrips else 0L
                            StatRow("TB Doanh thu/cuốc", FormatUtils.formatCurrency(avgRevPerTrip))
                            
                            if (totalKm > 0) {
                                val revenueWithKm = validKmEntries.sumOf { it.amount }
                                val avgRevPerKm = (revenueWithKm.toFloat() / totalKm).toLong()
                                StatRow("Doanh thu/km", "${FormatUtils.formatCurrency(avgRevPerKm)}/km")
                            }
                            if (totalHours > 0) {
                                val avgRevPerHr = totalRev / totalHours.toLong()
                                StatRow("Doanh thu/giờ", "${FormatUtils.formatCurrency(avgRevPerHr)}/giờ")
                            }"""

content = re.sub(stats_target, stats_replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)

