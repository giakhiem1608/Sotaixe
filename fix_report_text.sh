sed -i '149s/color = MaterialTheme.colorScheme.primary/color = onRevenueThemeColor.copy(alpha = 0.8f)/' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i '155s/color = MaterialTheme.colorScheme.onPrimaryContainer/color = onRevenueThemeColor/' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i '161s/color = MaterialTheme.colorScheme.onSurfaceVariant/color = onRevenueThemeColor.copy(alpha = 0.8f)/' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i '162s/color = MaterialTheme.colorScheme.primary/color = onRevenueThemeColor/' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i '165s/color = MaterialTheme.colorScheme.onSurfaceVariant/color = onRevenueThemeColor.copy(alpha = 0.8f)/' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i '166s/MaterialTheme.colorScheme.onSurfaceVariant/onRevenueThemeColor.copy(alpha = 0.8f)/' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i '110s/background(MaterialTheme.colorScheme.primary)/background(revenueThemeColor)/' app/src/main/java/com/example/ui/screens/ReportScreen.kt
