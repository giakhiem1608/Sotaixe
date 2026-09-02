sed -i '/var tripsStr by remember { mutableStateOf("1") }/a \        var distanceStr by remember { mutableStateOf("") }' app/src/main/java/com/example/ui/screens/TodayScreen.kt

sed -i '/OutlinedTextField(
                value = tripsStr/i \
            Spacer(modifier = Modifier.height(8.dp))\
            \
            OutlinedTextField(\
                value = distanceStr,\
                onValueChange = { distanceStr = it.replace(",", ".") },\
                label = { Text("Số km (Không bắt buộc)") },\
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),\
                modifier = Modifier.fillMaxWidth(),\
                singleLine = true\
            )\
' app/src/main/java/com/example/ui/screens/TodayScreen.kt

