import re

# ------------- AddRevenueSheet.kt -------------
with open("app/src/main/java/com/example/ui/screens/AddRevenueSheet.kt", "r") as f:
    content = f.read()

# Add tipStr
content = content.replace('var tripsStr by remember { mutableStateOf("1") }', 'var tripsStr by remember { mutableStateOf("1") }\n    var tipStr by remember { mutableStateOf("") }')

# Add tip text field
target_text_fields = r"""            OutlinedTextField\(\s*value = amountStr,\s*onValueChange = \{ amountStr = FormatUtils\.formatNumber\(it\.replace\("\.", ""\)\) \},\s*label = \{ Text\("Số tiền cuốc \(đ\)"\) \},\s*keyboardOptions = KeyboardOptions\(keyboardType = KeyboardType\.Number\),\s*modifier = Modifier\.fillMaxWidth\(\),\s*singleLine = true\s*\)\s*Spacer\(modifier = Modifier\.height\(8\.dp\)\)"""
replace_text_fields = """            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = FormatUtils.formatNumber(it.replace(".", "")) },
                label = { Text("Số tiền cuốc (đ)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = tipStr,
                onValueChange = { tipStr = FormatUtils.formatNumber(it.replace(".", "")) },
                label = { Text("Tiền tip (Tùy chọn)") },
                placeholder = { Text("0 đ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))"""
content = re.sub(r'            OutlinedTextField\(\s*value = amountStr,\s*onValueChange = \{ amountStr = FormatUtils\.formatNumber\(it\.replace\("\.", ""\)\) \},.*?modifier = Modifier\.fillMaxWidth\(\),\s*singleLine = true\s*\)\s*Spacer\(modifier = Modifier\.height\(8\.dp\)\)', replace_text_fields, content, flags=re.DOTALL)

# Update onSave lambda call
target_save = r"""val entry = RevenueEntry\(.*?\bamount = amount,\s*trips = trips,\s*distanceKm = (.*?),.*?\bnote = note,\s*timestamp = selectedTime.*?\).*?viewModel\.insertRevenueEntry\(entry\)"""
replace_save = r"""val entry = RevenueEntry(
                                sourceId = sources[selectedSourceIndex].id,
                                amount = amount,
                                tipAmount = tipStr.replace(".", "").toLongOrNull() ?: 0L,
                                trips = trips,
                                distanceKm = \1,
                                durationHrs = null,
                                note = note,
                                dateString = FormatUtils.formatDbDate(selectedTime),
                                timestamp = selectedTime
                            )
                            viewModel.insertRevenueEntry(entry)"""
content = re.sub(target_save, replace_save, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/AddRevenueSheet.kt", "w") as f:
    f.write(content)

