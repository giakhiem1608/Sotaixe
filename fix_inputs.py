import re

# Update TodayScreen
with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

target_today = r"""            OutlinedTextField\(\n                value = tripsStr,\n                onValueChange = \{ tripsStr = it \},\n                label = \{ Text\("Số cuốc"\) \},\n                keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\n                modifier = Modifier\.fillMaxWidth\(\),\n                singleLine = true\n            \)\n               \n            Spacer\(modifier = Modifier\.height\(8\.dp\)\)\n               \n            OutlinedTextField\(\n                value = distanceStr,\n                onValueChange = \{ distanceStr = it\.replace\(",", "\."\) \},\n                label = \{ Text\("Số km \(Không bắt buộc\)"\) \},\n                keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\n                modifier = Modifier\.fillMaxWidth\(\),\n                singleLine = true\n            \)"""

replace_today = """            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = tripsStr,
                    onValueChange = { tripsStr = it },
                    label = { Text("Số cuốc") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = distanceStr,
                    onValueChange = { distanceStr = it.replace(",", ".") },
                    label = { Text("Số KM") },
                    placeholder = { Text("Không bắt buộc") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }"""
content = re.sub(target_today, replace_today, content)
with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)

# Update EditSheets
with open("app/src/main/java/com/example/ui/screens/EditSheets.kt", "r") as f:
    content = f.read()
    
target_edit = r"""            OutlinedTextField\(\n                value = tripsStr,\n                onValueChange = \{ tripsStr = it \},\n                label = \{ Text\("Số cuốc"\) \},\n                keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\n                modifier = Modifier\.fillMaxWidth\(\),\n                singleLine = true\n            \)\n                        \n            Spacer\(modifier = Modifier\.height\(8\.dp\)\)\n                        \n            OutlinedTextField\(\n                value = distanceStr,\n                onValueChange = \{ distanceStr = it\.replace\(",", "\."\) \},\n                label = \{ Text\("Số km \(Không bắt buộc\)"\) \},\n                keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\n                modifier = Modifier\.fillMaxWidth\(\),\n                singleLine = true\n            \)"""

replace_edit = """            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = tripsStr,
                    onValueChange = { tripsStr = it },
                    label = { Text("Số cuốc") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = distanceStr,
                    onValueChange = { distanceStr = it.replace(",", ".") },
                    label = { Text("Số KM") },
                    placeholder = { Text("Không bắt buộc") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }"""
content = re.sub(target_edit, replace_edit, content)

# And fix distanceKm save in EditRevenueSheet
save_target = r"""                                trips = trips,\n                                note = note\n                            \}\)"""
save_replace = """                                trips = trips,\n                                distanceKm = distanceStr.toFloatOrNull(),\n                                note = note\n                            })"""
content = re.sub(save_target, save_replace, content)

with open("app/src/main/java/com/example/ui/screens/EditSheets.kt", "w") as f:
    f.write(content)

