import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

# Collect tip flow
target_colors = r"""val expenseColorHex by viewModel\.expenseColor\.collectAsState\(\)\n    val cardBgColor ="""
replace_colors = """val expenseColorHex by viewModel.expenseColor.collectAsState()
    val tipColorHex by viewModel.tipColor.collectAsState()
    val cardBgColor ="""
content = re.sub(target_colors, replace_colors, content)

target_color2 = r"""val expColor = if \(expenseColorHex\.isNotEmpty\(\)\) try \{ Color\(android\.graphics\.Color\.parseColor\(expenseColorHex\)\) \} catch \(e: Exception\) \{ ExpenseError \} else ExpenseError"""
replace_color2 = """val expColor = if (expenseColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expenseColorHex)) } catch (e: Exception) { ExpenseError } else ExpenseError
    val tipColor = if (tipColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(tipColorHex)) } catch (e: Exception) { Color(0xFFF59E0B) } else Color(0xFFF59E0B)"""
content = re.sub(target_color2, replace_color2, content)

# Compute tip totals
target_totals = r"""    val totalRev = revenueEntries\.sumOf \{ it\.amount \}\n    val totalExp = expenseEntries\.sumOf \{ it\.amount \}\n    val netIncome = totalRev - totalExp"""
replace_totals = """    val totalRev = revenueEntries.sumOf { it.amount }
    val totalTip = revenueEntries.sumOf { it.tipAmount ?: 0L }
    val totalExp = expenseEntries.sumOf { it.amount }
    val netIncome = totalRev + totalTip - totalExp"""
content = re.sub(target_totals, replace_totals, content)

# Update Hero card design
target_hero = r"""                                Text\(FormatUtils\.formatCurrency\(totalRev\), fontWeight = FontWeight\.Bold, color = revenueColor\)\n                            \}\n                            Column\(horizontalAlignment = Alignment\.End\) \{\n                                Text\("Chi phí", style = MaterialTheme\.typography\.bodySmall, color = onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                                Text\(if \(totalExp > 0\) "- " \+ FormatUtils\.formatCurrency\(totalExp\) else "0 đ", fontWeight = FontWeight\.Bold, color = if \(totalExp > 0\) expColor else onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                            \}\n                        \}\n                    \}\n                \}"""
replace_hero = """                                Text(FormatUtils.formatCurrency(totalRev), fontWeight = FontWeight.Bold, color = revenueColor)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Chi phí", style = MaterialTheme.typography.bodySmall, color = onCardBgColor.copy(alpha = 0.8f))
                                Text(if (totalExp > 0) "- " + FormatUtils.formatCurrency(totalExp) else "0 đ", fontWeight = FontWeight.Bold, color = if (totalExp > 0) expColor else onCardBgColor.copy(alpha = 0.8f))
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
                    }
                }"""
content = re.sub(target_hero, replace_hero, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)
