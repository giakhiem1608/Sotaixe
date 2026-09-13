import re

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

# Add tipColorHex flow
target_flow = r"""val expenseColorHex by viewModel\.expenseColor\.collectAsState\(\)"""
replace_flow = """val expenseColorHex by viewModel.expenseColor.collectAsState()
    val tipColorHex by viewModel.tipColor.collectAsState()
    val totalTip by viewModel.todaysTotalTip.collectAsState()"""
content = re.sub(target_flow, replace_flow, content)

# Add tipColor
target_color = r"""val expColor = if \(expenseColorHex.*?ExpenseError"""
replace_color = """val expColor = if (expenseColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expenseColorHex)) } catch (e: Exception) { ExpenseError } else ExpenseError
    val tipColor = if (tipColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(tipColorHex)) } catch (e: Exception) { Color(0xFFF59E0B) } else Color(0xFFF59E0B)"""
content = re.sub(target_color, replace_color, content)

# Update Hero Card content
target_hero = r"""Row\(\n                    modifier = Modifier\.fillMaxWidth\(\),\n                    horizontalArrangement = Arrangement\.SpaceBetween\n                \) \{\n                    Column\(horizontalAlignment = Alignment\.Start\) \{\n                        Text\("Doanh thu", style = MaterialTheme\.typography\.bodySmall, color = onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                        Text\(FormatUtils\.formatCurrency\(totalRevenue\), fontWeight = FontWeight\.Bold, color = revenueColor\)\n                    \}\n                    Column\(horizontalAlignment = Alignment\.End\) \{\n                        Text\("Chi phí", style = MaterialTheme\.typography\.bodySmall, color = onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                        Text\(FormatUtils\.formatCurrency\(totalExpense\), fontWeight = FontWeight\.Bold, color = if \(totalExpense > 0\) expColor else onCardBgColor\.copy\(alpha = 0\.8f\)\)\n                    \}\n                \}\n\n                Spacer\(modifier = Modifier\.height\(12\.dp\)\)\n                HorizontalDivider\(color = onCardBgColor\.copy\(alpha = 0\.2f\)\)\n                Spacer\(modifier = Modifier\.height\(8\.dp\)\)"""

replace_hero = """Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Doanh thu", style = MaterialTheme.typography.bodySmall, color = onCardBgColor.copy(alpha = 0.8f))
                        Text(FormatUtils.formatCurrency(totalRevenue), fontWeight = FontWeight.Bold, color = revenueColor)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Chi phí", style = MaterialTheme.typography.bodySmall, color = onCardBgColor.copy(alpha = 0.8f))
                        Text(if (totalExpense > 0) "- ${FormatUtils.formatCurrency(totalExpense)}" else "0 đ", fontWeight = FontWeight.Bold, color = if (totalExpense > 0) expColor else onCardBgColor.copy(alpha = 0.8f))
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
                HorizontalDivider(color = onCardBgColor.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))"""
content = re.sub(target_hero, replace_hero, content, flags=re.DOTALL)

# Change "Cơ cấu doanh thu" to "Nguồn doanh thu"
content = content.replace('Text("Cơ cấu doanh thu"', 'Text("Nguồn doanh thu"')

# CTA button updates: white surface for expense, primary for revenue, no heavy shadows.
target_cta = r"""Button\(\s*onClick = \{ showAddExpenseSheet = true \},\s*modifier = Modifier\s*\.weight\(1f\)\s*\.height\(52\.dp\),\s*colors = ButtonDefaults\.buttonColors\(\s*containerColor = MaterialTheme\.colorScheme\.surface,\s*contentColor = MaterialTheme\.colorScheme\.primary\s*\),\s*elevation = ButtonDefaults\.buttonElevation\(defaultElevation = 1\.dp\),\s*shape = RoundedCornerShape\(12\.dp\)\s*\) \{\s*Text\("\+ Chi phí", fontWeight = FontWeight\.Bold, maxLines = 1, softWrap = false\)\s*\}\s*Button\(\s*onClick = \{ showAddRevenueSheet = true \},\s*modifier = Modifier\s*\.weight\(1f\)\s*\.height\(52\.dp\),\s*colors = ButtonDefaults\.buttonColors\(\s*containerColor = MaterialTheme\.colorScheme\.primary,\s*contentColor = MaterialTheme\.colorScheme\.onPrimary\s*\),\s*elevation = ButtonDefaults\.buttonElevation\(defaultElevation = 2\.dp\),\s*shape = RoundedCornerShape\(12\.dp\)\s*\) \{\s*Text\("\+ Doanh thu", fontWeight = FontWeight\.Bold, maxLines = 1, softWrap = false\)\s*\}"""

replace_cta = """OutlinedButton(
                onClick = { showAddExpenseSheet = true },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("+ Chi phí", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
            }
            Button(
                onClick = { showAddRevenueSheet = true },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Doanh thu", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
            }"""
content = re.sub(target_cta, replace_cta, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)

