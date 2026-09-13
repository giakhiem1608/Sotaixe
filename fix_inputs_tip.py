import re

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

# Add tipStr
content = content.replace('var tripsStr by remember { mutableStateOf("1") }', 'var tripsStr by remember { mutableStateOf("1") }\n        var tipStr by remember { mutableStateOf("") }')

# Add Tip field
target_revenue_amount = r"""            OutlinedTextField\(\s*value = amountStr,\s*onValueChange = \{\s*newValue ->\s*if \(newValue\.all \{ it\.isDigit\(\) \}\) amountStr = newValue\s*\},\s*label = \{ Text\("Số tiền thực nhận \(đ\)"\) \},\s*keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\s*visualTransformation = com\.example\.utils\.CurrencyVisualTransformation\(\),\s*modifier = Modifier\.fillMaxWidth\(\),\s*singleLine = true\s*\)\s*Spacer\(modifier = Modifier\.height\(8\.dp\)\)"""
replace_revenue_amount = """            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) amountStr = newValue
                },
                label = { Text("Số tiền cuốc (đ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tipStr,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) tipStr = newValue
                },
                label = { Text("Tiền tip (Tùy chọn)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))"""
content = re.sub(target_revenue_amount, replace_revenue_amount, content, flags=re.DOTALL)

# Update onSave 
target_revenue_save = r"""val amount = amountStr\.toLongOrNull\(\) \?: 0L\s*val trips = tripsStr\.toIntOrNull\(\) \?: 1\s*if \(amount > 0\) \{\s*val entry = RevenueEntry\(\s*sourceId = selectedSourceId,\s*amount = amount,\s*trips = trips,\s*distanceKm = distanceStr\.replace\(",", "\."\)\.toFloatOrNull\(\),\s*note = note,\s*dateString = dateStr,\s*timestamp = System\.currentTimeMillis\(\)\s*\)"""
replace_revenue_save = """val amount = amountStr.toLongOrNull() ?: 0L
                            val tipAmount = tipStr.toLongOrNull() ?: 0L
                            val trips = tripsStr.toIntOrNull() ?: 1
                            if (amount > 0) {
                                val entry = RevenueEntry(
                                    sourceId = selectedSourceId,
                                    amount = amount,
                                    tipAmount = tipAmount,
                                    trips = trips,
                                    distanceKm = distanceStr.replace(",", ".").toFloatOrNull(),
                                    note = note,
                                    dateString = dateStr,
                                    timestamp = System.currentTimeMillis()
                                )"""
content = re.sub(target_revenue_save, replace_revenue_save, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)

