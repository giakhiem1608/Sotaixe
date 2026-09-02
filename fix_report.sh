sed -i '/val netIncome = totalRev - totalExp/a \    val revenueThemeColorHex by viewModel.revenueThemeColor.collectAsState()\n    val revenueThemeColor = try { Color(android.graphics.Color.parseColor(revenueThemeColorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primaryContainer }\n    val onRevenueThemeColor = if (androidx.compose.ui.graphics.luminance(revenueThemeColor) > 0.5f) Color.Black else Color.White' app/src/main/java/com/example/ui/screens/ReportScreen.kt

sed -i 's/modifier = Modifier.fillMaxWidth().clickable { showGoalDialog = true },/modifier = Modifier.fillMaxWidth().clickable { showGoalDialog = true },/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
# Wait, I didn't change anything in the line above. 
# Let's target the summary card
sed -i 's/colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)/colors = CardDefaults.cardColors(containerColor = revenueThemeColor)/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt

