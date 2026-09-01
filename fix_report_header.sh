sed -i '44i \    var showMonthPicker by remember { mutableStateOf(false) }' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i 's/            Text(/            TextButton(onClick = { showMonthPicker = true }) {\n                Text(/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i 's/fontWeight = FontWeight.Bold/fontWeight = FontWeight.Bold\n                )\n            }/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
