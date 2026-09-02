with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'Text(FormatUtils.formatCurrency(totalExpense), fontWeight = FontWeight.Bold, color = if (totalExpense > 0) ExpenseError else onCardBgColor.copy(alpha = 0.8f))',
    'Text(FormatUtils.formatCurrency(totalExpense), fontWeight = FontWeight.Bold, color = if (totalExpense > 0) expColor else onCardBgColor.copy(alpha = 0.8f))'
)

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)
