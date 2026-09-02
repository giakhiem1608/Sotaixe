sed -i 's/Text("Tổng thu nhập", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)/Text("Tổng thu nhập", style = MaterialTheme.typography.labelLarge, color = onRevenueThemeColor.copy(alpha = 0.8f))/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt

sed -i 's/color = MaterialTheme.colorScheme.onPrimaryContainer/color = onRevenueThemeColor/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt

sed -i 's/Text("Doanh thu", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)/Text("Doanh thu", style = MaterialTheme.typography.bodySmall, color = onRevenueThemeColor.copy(alpha = 0.8f))/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt

sed -i 's/Text(FormatUtils.formatCurrency(totalRev), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)/Text(FormatUtils.formatCurrency(totalRev), fontWeight = FontWeight.Bold, color = onRevenueThemeColor)/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt

sed -i 's/Text("Chi phí", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)/Text("Chi phí", style = MaterialTheme.typography.bodySmall, color = onRevenueThemeColor.copy(alpha = 0.8f))/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt

sed -i 's/color = if (totalExp > 0) ExpenseError else MaterialTheme.colorScheme.onSurfaceVariant/color = if (totalExp > 0) ExpenseError else onRevenueThemeColor.copy(alpha = 0.8f)/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt

sed -i 's/Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(percent).background(MaterialTheme.colorScheme.primary))/Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(percent).background(revenueThemeColor))/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
