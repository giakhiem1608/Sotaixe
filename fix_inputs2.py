import re

# Update TodayScreen
with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

target_today = r"""            OutlinedTextField\(\s*value = tripsStr,\s*onValueChange = \{ tripsStr = it \},\s*label = \{ Text\("Số cuốc"\) \},\s*keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\s*modifier = Modifier\.fillMaxWidth\(\),\s*singleLine = true\s*\)\s*Spacer\(modifier = Modifier\.height\(8\.dp\)\)\s*OutlinedTextField\(\s*value = distanceStr,\s*onValueChange = \{ distanceStr = it\.replace\(",", "\."\) \},\s*label = \{ Text\("Số km \(Không bắt buộc\)"\) \},\s*keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\s*modifier = Modifier\.fillMaxWidth\(\),\s*singleLine = true\s*\)"""

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
    
target_edit = r"""            OutlinedTextField\(\s*value = tripsStr,\s*onValueChange = \{ tripsStr = it \},\s*label = \{ Text\("Số cuốc"\) \},\s*keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\s*modifier = Modifier\.fillMaxWidth\(\),\s*singleLine = true\s*\)\s*Spacer\(modifier = Modifier\.height\(8\.dp\)\)\s*OutlinedTextField\(\s*value = distanceStr,\s*onValueChange = \{ distanceStr = it\.replace\(",", "\."\) \},\s*label = \{ Text\("Số km \(Không bắt buộc\)"\) \},\s*keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\s*modifier = Modifier\.fillMaxWidth\(\),\s*singleLine = true\s*\)"""

content = re.sub(target_edit, replace_today, content)

save_target = r"""                                trips = trips,\s*note = note\s*\)\)"""
save_replace = """                                trips = trips,\n                                distanceKm = distanceStr.replace(",", ".").toFloatOrNull(),\n                                note = note\n                            ))"""
content = re.sub(save_target, save_replace, content)

with open("app/src/main/java/com/example/ui/screens/EditSheets.kt", "w") as f:
    f.write(content)

