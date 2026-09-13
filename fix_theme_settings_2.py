import re

with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "r") as f:
    content = f.read()

target = r"""                    ThemeColorPickerSheet\(\n                        initialColorHex = colorPickerCurrentHex,\n                        onColorSelected = \{ hex ->\n                            when \(colorPickerTarget\) \{\n                                "bg" -> tempBgHex = hex\n                                "income" -> tempIncomeHex = hex\n                                "rev" -> tempRevHex = hex\n                                "exp" -> tempExpHex = hex\n                                "tip" -> tempTipHex = hex\n                            \}\n                            showColorPicker = false\n                        \}\n                    \)"""

replace = """                    val presetColors = listOf(
                        "#111827", "#16A34A", "#22C55E", "#3B82F6", 
                        "#8B5CF6", "#F05D5E", "#F59E0B", "#FFFFFF",
                        "#4C1D95", "#0369A1", "#0F172A", "#64748B"
                    )
                    @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        presetColors.forEach { hex ->
                            val color = try { Color(android.graphics.Color.parseColor(hex)) } catch(e: Exception) { Color.Transparent }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable {
                                        when (colorPickerTarget) {
                                            "bg" -> tempBgHex = hex
                                            "income" -> tempIncomeHex = hex
                                            "rev" -> tempRevHex = hex
                                            "exp" -> tempExpHex = hex
                                            "tip" -> tempTipHex = hex
                                        }
                                        showColorPicker = false
                                    }
                            )
                        }
                    }"""
content = re.sub(target, replace, content)

with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "w") as f:
    f.write(content)
