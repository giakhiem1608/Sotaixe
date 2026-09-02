import re

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

# Replace revenueThemeColorHex with cardBgColor, incomeColor, revenueColor, expenseColor
content = content.replace(
'''    val revenueThemeColorHex by viewModel.revenueThemeColor.collectAsState()
    val revenueThemeColor = try { Color(android.graphics.Color.parseColor(revenueThemeColorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primaryContainer }
    val onRevenueThemeColor = if (revenueThemeColor.luminance() > 0.5f) Color.Black else Color.White
    var showThemePicker by remember { mutableStateOf(false) }''',
'''    val cardBgColorHex by viewModel.cardBgColor.collectAsState()
    val incomeColorHex by viewModel.incomeColor.collectAsState()
    val revenueColorHex by viewModel.revenueColor.collectAsState()
    val expenseColorHex by viewModel.expenseColor.collectAsState()
    
    val cardBgColor = try { Color(android.graphics.Color.parseColor(cardBgColorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primaryContainer }
    val onCardBgColor = if (cardBgColor.luminance() > 0.5f) Color.Black else Color.White
    
    val incomeColor = if (incomeColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(incomeColorHex)) } catch (e: Exception) { onCardBgColor } else onCardBgColor
    val revenueColor = if (revenueColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(revenueColorHex)) } catch (e: Exception) { onCardBgColor } else onCardBgColor
    val expColor = if (expenseColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expenseColorHex)) } catch (e: Exception) { ExpenseError } else ExpenseError'''
)

# Remove ThemeColorPickerSheet
content = re.sub(r'if \(showThemePicker\).*?onDismiss = \{ showThemePicker = false \}\n        \)\n    \}', '', content, flags=re.DOTALL)

# Replace revenueThemeColor with cardBgColor
content = content.replace('revenueThemeColor', 'cardBgColor')

# Replace onRevenueThemeColor with onCardBgColor
content = content.replace('onRevenueThemeColor', 'onCardBgColor')

# Replace pointerInput on Card
content = content.replace('.pointerInput(Unit) { detectTapGestures(onLongPress = { showThemePicker = true }) }', '')

# Replace text color for Income
content = re.sub(
    r'Text\(\s*text = FormatUtils\.formatCurrency\(netIncome\),\s*style = MaterialTheme\.typography\.displayMedium\.copy\(fontSize = 40\.sp\),\s*fontWeight = FontWeight\.Bold,\s*color = onCardBgColor\s*\)',
    '''Text(
                        text = FormatUtils.formatCurrency(netIncome),
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 40.sp),
                        fontWeight = FontWeight.Bold,
                        color = incomeColor
                    )''', content
)

# Replace text color for Revenue
content = re.sub(
    r'Text\(FormatUtils\.formatCurrency\(totalRevenue\), fontWeight = FontWeight\.Bold, color = onCardBgColor\)',
    'Text(FormatUtils.formatCurrency(totalRevenue), fontWeight = FontWeight.Bold, color = revenueColor)',
    content
)

# Replace text color for Expense
content = re.sub(
    r'Text\(FormatUtils\.formatCurrency\(totalExpense\), fontWeight = FontWeight\.Bold, color = ExpenseError\)',
    'Text(FormatUtils.formatCurrency(totalExpense), fontWeight = FontWeight.Bold, color = expColor)',
    content
)

# Replace 'color = ExpenseError' in chi phí text block
content = content.replace(
    'Text("- " + FormatUtils.formatCurrency(totalExpense), fontWeight = FontWeight.Bold, color = ExpenseError)',
    'Text("- " + FormatUtils.formatCurrency(totalExpense), fontWeight = FontWeight.Bold, color = expColor)'
)

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)
