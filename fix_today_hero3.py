import re

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

target_hero = r"""                    Column\(horizontalAlignment = Alignment\.End\) \{\n                        Text\("Chi phí", style = MaterialTheme\.typography\.bodySmall, color = onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                        Text\(FormatUtils\.formatCurrency\(totalExpense\), fontWeight = FontWeight\.Bold, color = if \(totalExpense > 0\) expColor else onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                    \}\n                \}\n                                Spacer\(modifier = Modifier\.height\(12\.dp\)\)\n                HorizontalDivider\(color = onCardBgColor\.copy\(alpha = 0\.2f\)\)"""
replace_hero = """                    Column(horizontalAlignment = Alignment.End) {
                        Text("Chi phí", style = MaterialTheme.typography.bodySmall, color = onCardBgColor.copy(alpha = 0.8f))
                        Text(if (totalExpense > 0) "- " + FormatUtils.formatCurrency(totalExpense) else "0 đ", fontWeight = FontWeight.Bold, color = if (totalExpense > 0) expColor else onCardBgColor.copy(alpha = 0.8f))
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Tip", style = MaterialTheme.typography.bodySmall, color = onCardBgColor.copy(alpha = 0.8f))
                        Text(if (totalTip > 0) "+ ${FormatUtils.formatCurrency(totalTip)}" else "0 đ", fontWeight = FontWeight.Bold, color = if (totalTip > 0) tipColor else onCardBgColor.copy(alpha = 0.8f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = onCardBgColor.copy(alpha = 0.2f))"""
content = re.sub(target_hero, replace_hero, content)

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)

