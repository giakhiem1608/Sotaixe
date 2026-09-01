sed -i 's/Text("Cơ Cấu Nguồn Thu"/Text("Cơ cấu nguồn thu"/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i 's/Text("Nhận Xét Thông Minh"/Text("Nhận xét thông minh"/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i 's/"• Dựa trên dữ liệu ít ỏi hiện có:"/"Dựa trên dữ liệu hiện có:"/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i 's/if (daysWorked < 3) {//g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i 's/    Text("Dựa trên dữ liệu hiện có:".*/    Text("Dựa trên dữ liệu hiện có:", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
