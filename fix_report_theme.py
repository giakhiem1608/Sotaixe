with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

content = content.replace('val revenueThemeColorHex by viewModel.revenueThemeColor.collectAsState()', 'val cardBgColorHex by viewModel.cardBgColor.collectAsState()\n    val incomeColorHex by viewModel.incomeColor.collectAsState()\n    val revenueColorHex by viewModel.revenueColor.collectAsState()\n    val expenseColorHex by viewModel.expenseColor.collectAsState()')
content = content.replace('revenueThemeColorHex', 'cardBgColorHex')
content = content.replace('revenueThemeColor', 'cardBgColor')
content = content.replace('onRevenueThemeColor', 'onCardBgColor')

content = content.replace(
'''val cardBgColor = try { Color(android.graphics.Color.parseColor(cardBgColorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primaryContainer }
    val onCardBgColor = if (cardBgColor.luminance() > 0.5f) Color.Black else Color.White''',
'''val cardBgColor = try { Color(android.graphics.Color.parseColor(cardBgColorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primaryContainer }
    val onCardBgColor = if (cardBgColor.luminance() > 0.5f) Color.Black else Color.White
    val incomeColor = if (incomeColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(incomeColorHex)) } catch (e: Exception) { onCardBgColor } else onCardBgColor
    val revenueColor = if (revenueColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(revenueColorHex)) } catch (e: Exception) { onCardBgColor } else onCardBgColor
    val expColor = if (expenseColorHex.isNotEmpty()) try { Color(android.graphics.Color.parseColor(expenseColorHex)) } catch (e: Exception) { ExpenseError } else ExpenseError'''
)

# Text replacements for Summary Card
content = content.replace(
    'Text("Tổng thu nhập", style = MaterialTheme.typography.labelLarge, color = onCardBgColor.copy(alpha = 0.8f))',
    'Text("Tổng thu nhập", style = MaterialTheme.typography.labelLarge, color = incomeColor)'
)
content = content.replace(
    'style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),\n                                fontWeight = FontWeight.Bold,\n                                color = onCardBgColor',
    'style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),\n                                fontWeight = FontWeight.Bold,\n                                color = incomeColor'
)
content = content.replace(
    'Text(FormatUtils.formatCurrency(totalRev), fontWeight = FontWeight.Bold, color = onCardBgColor)',
    'Text(FormatUtils.formatCurrency(totalRev), fontWeight = FontWeight.Bold, color = revenueColor)'
)
content = content.replace(
    'Text(FormatUtils.formatCurrency(totalExp), fontWeight = FontWeight.Bold, color = if (totalExp > 0) ExpenseError else onCardBgColor.copy(alpha = 0.8f))',
    'Text(FormatUtils.formatCurrency(totalExp), fontWeight = FontWeight.Bold, color = if (totalExp > 0) expColor else onCardBgColor.copy(alpha = 0.8f))'
)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)
