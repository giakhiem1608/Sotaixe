import re

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'var tripsStr by remember { mutableStateOf("1") }',
    'var tripsStr by remember { mutableStateOf("1") }\n        var distanceStr by remember { mutableStateOf("") }'
)

new_field = """            OutlinedTextField(
                value = tripsStr,
                onValueChange = { tripsStr = it },
                label = { Text("Số cuốc") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = distanceStr,
                onValueChange = { distanceStr = it.replace(",", ".") },
                label = { Text("Số km (Không bắt buộc)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )"""
            
content = content.replace(
    """            OutlinedTextField(
                value = tripsStr,
                onValueChange = { tripsStr = it },
                label = { Text("Số cuốc") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )""",
    new_field
)

content = content.replace(
    'onSave(selectedSourceId, amount, trips, null, null, note)',
    'onSave(selectedSourceId, amount, trips, null, distanceStr.toFloatOrNull(), note)'
)

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)
