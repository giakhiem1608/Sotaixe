sed -i 's/val breakdown = sources.map { source ->/val breakdownList = sources.map { source ->/g' app/src/main/java/com/example/ui/screens/TodayScreen.kt
sed -i '/Row(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))) {/,/}/d' app/src/main/java/com/example/ui/screens/TodayScreen.kt
sed -i '/LazyColumn(/,/}/d' app/src/main/java/com/example/ui/screens/TodayScreen.kt
sed -i 's/breakdownList.forEach/breakdown.forEach/g' app/src/main/java/com/example/ui/screens/TodayScreen.kt
