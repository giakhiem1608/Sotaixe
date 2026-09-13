import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

target = r"""                            Row\(modifier = Modifier\.fillMaxWidth\(\), horizontalArrangement = Arrangement\.SpaceBetween\) \{\n                                Column\(horizontalAlignment = Alignment\.Start\) \{\n                                    Text\("Doanh thu", style = MaterialTheme\.typography\.bodySmall, color = onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                                    Text\(FormatUtils\.formatCurrency\(totalRev\), fontWeight = FontWeight\.Bold, color = revenueColor\)\n                                \}\n                                Column\(horizontalAlignment = Alignment\.End\) \{\n                                    Text\("Chi phí", style = MaterialTheme\.typography\.bodySmall, color = onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                                    Text\(FormatUtils\.formatCurrency\(totalExp\), fontWeight = FontWeight\.Bold, color = if \(totalExp > 0\) expColor else onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                                \}\n                            \}\n                        \}\n                    \}\n                \}"""

replace = """                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text("Doanh thu", style = MaterialTheme.typography.bodySmall, color = onCardBgColor.copy(alpha = 0.8f))
                                    Text(FormatUtils.formatCurrency(totalRev), fontWeight = FontWeight.Bold, color = revenueColor)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Chi phí", style = MaterialTheme.typography.bodySmall, color = onCardBgColor.copy(alpha = 0.8f))
                                    Text(if (totalExp > 0) "- ${FormatUtils.formatCurrency(totalExp)}" else "0 đ", fontWeight = FontWeight.Bold, color = if (totalExp > 0) expColor else onCardBgColor.copy(alpha = 0.8f))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text("Tip", style = MaterialTheme.typography.bodySmall, color = onCardBgColor.copy(alpha = 0.8f))
                                    Text(if (totalTip > 0) "+ ${FormatUtils.formatCurrency(totalTip)}" else "0 đ", fontWeight = FontWeight.Bold, color = if (totalTip > 0) tipColor else onCardBgColor.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }"""
content = re.sub(target, replace, content)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)
