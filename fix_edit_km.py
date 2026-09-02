import re

with open("app/src/main/java/com/example/ui/screens/EditSheets.kt", "r") as f:
    content = f.read()

content = content.replace(
    'var tripsStr by remember { mutableStateOf(entry.trips.toString()) }',
    'var tripsStr by remember { mutableStateOf(entry.trips.toString()) }\n        var distanceStr by remember { mutableStateOf(entry.distanceKm?.toString() ?: "") }'
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
    'onSave(entry.copy(sourceId = selectedSourceId, amount = amount, trips = trips, note = note))',
    'onSave(entry.copy(sourceId = selectedSourceId, amount = amount, trips = trips, distanceKm = distanceStr.toFloatOrNull(), note = note))'
)

with open("app/src/main/java/com/example/ui/screens/EditSheets.kt", "w") as f:
    f.write(content)
