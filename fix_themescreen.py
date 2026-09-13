import re

with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "r") as f:
    content = f.read()

# Add tempTipHex to state variables
content = content.replace("var tempExpHex by remember { mutableStateOf(currentExpHex) }", "var tempExpHex by remember { mutableStateOf(currentExpHex) }\n    val currentTipHex by viewModel.tipColor.collectAsState()\n    var tempTipHex by remember { mutableStateOf(currentTipHex) }")

# Add tempTipHex to ColorSelectRow
target_select_row = r"""                ColorSelectRow\("Chi phí", tempExpHex, allowAuto = true\) \{ \n                    colorPickerTarget = "exp"\n                    colorPickerCurrentHex = tempExpHex\n                    showColorPicker = true \n                \}"""
replace_select_row = """                ColorSelectRow("Chi phí", tempExpHex, allowAuto = true) { 
                    colorPickerTarget = "exp"
                    colorPickerCurrentHex = tempExpHex
                    showColorPicker = true 
                }
                ColorSelectRow("Tip", tempTipHex, allowAuto = true) { 
                    colorPickerTarget = "tip"
                    colorPickerCurrentHex = tempTipHex
                    showColorPicker = true 
                }"""
content = content.replace('                ColorSelectRow("Chi phí", tempExpHex, allowAuto = true) { \n                    colorPickerTarget = "exp"\n                    colorPickerCurrentHex = tempExpHex\n                    showColorPicker = true \n                }', replace_select_row)

# Add tempTipHex to isSelected logic
content = content.replace("val isSelected = tempBgHex == preset.bgHex && tempIncomeHex == preset.incomeHex && tempRevHex == preset.revHex && tempExpHex == preset.expHex", "val isSelected = tempBgHex == preset.bgHex && tempIncomeHex == preset.incomeHex && tempRevHex == preset.revHex && tempExpHex == preset.expHex && tempTipHex == preset.tipHex")

# Add preset selection assignment
content = content.replace("tempExpHex = preset.expHex\n                                }", "tempExpHex = preset.expHex\n                                    tempTipHex = preset.tipHex\n                                }")

# Update ViewModel call
content = content.replace("viewModel.updateCardColors(tempBgHex, tempIncomeHex, tempRevHex, tempExpHex)", "viewModel.updateCardColors(tempBgHex, tempIncomeHex, tempRevHex, tempExpHex, tempTipHex)")

# Update ThemePreviewCard declaration and invocation
content = content.replace("ThemePreviewCard(\n                bgHex = tempBgHex,\n                incomeHex = tempIncomeHex,\n                revHex = tempRevHex,\n                expHex = tempExpHex\n            )", "ThemePreviewCard(\n                bgHex = tempBgHex,\n                incomeHex = tempIncomeHex,\n                revHex = tempRevHex,\n                expHex = tempExpHex,\n                tipHex = tempTipHex\n            )")

content = content.replace('fun ThemePreviewCard(bgHex: String, incomeHex: String, revHex: String, expHex: String) {', 'fun ThemePreviewCard(bgHex: String, incomeHex: String, revHex: String, expHex: String, tipHex: String) {')

content = content.replace('val expColor = if (expHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expHex)) } catch (e: Exception) { com.example.ui.theme.ExpenseError } else com.example.ui.theme.ExpenseError', 'val expColor = if (expHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expHex)) } catch (e: Exception) { com.example.ui.theme.ExpenseError } else com.example.ui.theme.ExpenseError\n    val tipColor = if (tipHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(tipHex)) } catch (e: Exception) { Color(0xFFF59E0B) } else Color(0xFFF59E0B)')

# Update ThemePreviewCard design
new_preview = """    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("THU NHẬP HÔM NAY", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
            Text(
                text = "170.000 ₫",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                fontWeight = FontWeight.Bold,
                color = incomeColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Doanh thu", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
                    Text("750.000 ₫", fontWeight = FontWeight.Bold, color = revColor)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Chi phí", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
                    Text("- 600.000 ₫", fontWeight = FontWeight.Bold, color = expColor)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text("Tip", style = MaterialTheme.typography.labelMedium, color = autoOnColor.copy(alpha = 0.8f))
                Text("+ 20.000 ₫", fontWeight = FontWeight.Bold, color = tipColor)
            }
        }
    }"""
    
content = re.sub(r'    Card\(\s*modifier = Modifier\.fillMaxWidth\(\).*?            \}\n        \}\n    \}', new_preview, content, flags=re.DOTALL)

# Add "tip" to color picker targets
content = content.replace('"exp" -> tempExpHex = hex', '"exp" -> tempExpHex = hex\n                                            "tip" -> tempTipHex = hex')

# Add "Reset to default" button
reset_btn = """            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Mẫu có sẵn", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Row {
                    TextButton(onClick = { 
                        tempBgHex = "#111827"
                        tempIncomeHex = "#FFFFFF"
                        tempRevHex = "#22C55E"
                        tempExpHex = "#F05D5E"
                        tempTipHex = "#F59E0B"
                    }) {
                        Text("Reset", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = { isCustomMode = !isCustomMode }) {
                        Text(if (isCustomMode) "Chọn mẫu >" else "Tùy chỉnh >")
                    }
                }
            }"""
content = re.sub(r'            Row\(modifier = Modifier\.fillMaxWidth\(\), horizontalArrangement = Arrangement\.SpaceBetween, verticalAlignment = Alignment\.CenterVertically\) \{\s*Text\("Mẫu có sẵn".*?Text\(if \(isCustomMode\) "Chọn mẫu >" else "Tùy chỉnh >"\)\s*\}\s*\}', reset_btn, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "w") as f:
    f.write(content)
