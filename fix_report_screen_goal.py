import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

target = """                // Goals Card
                item {
                    val goal by viewModel.currentMonthGoal.collectAsState()
                    var showGoalDialog by remember { mutableStateOf(false) }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { showGoalDialog = true },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mục tiêu tháng $displayMonth", fontWeight = FontWeight.Bold)
                                Icon(Icons.Filled.Edit, contentDescription = "Sửa mục tiêu", modifier = Modifier.size(20.dp))
                            }
                            
                            if (goal == null) {
                                Text("Chưa đặt mục tiêu. Chạm để thiết lập.", color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f), modifier = Modifier.padding(top = 8.dp))
                            } else {
                                val currentAmount = if (goal!!.type == "REVENUE") totalRev else netIncome
                                val percent = if (goal!!.amount > 0) (currentAmount.toFloat() / goal!!.amount.toFloat()).coerceIn(0f, 1f) else 0f
                                val remain = goal!!.amount - currentAmount
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                                ) {
                                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(percent).background(cardBgColor))
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (remain > 0) "Còn thiếu ${FormatUtils.formatCurrency(remain)} để đạt mục tiêu" else "Đã đạt mục tiêu. Chúc mừng bạn!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                    
                    if (showGoalDialog) {
                        GoalDialog(viewModel = viewModel, onDismiss = { showGoalDialog = false })
                    }
                }"""

replacement = """                // Goals Card
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
                                    Text("Mục tiêu tháng $displayMonth", fontWeight = FontWeight.Bold)
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
                }"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)

