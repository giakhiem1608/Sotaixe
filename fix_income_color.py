with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    '''Text(
                    text = FormatUtils.formatCurrency(netIncome),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),
                    fontWeight = FontWeight.Bold,
                    color = onCardBgColor
                )''',
    '''Text(
                    text = FormatUtils.formatCurrency(netIncome),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 32.sp),
                    fontWeight = FontWeight.Bold,
                    color = incomeColor
                )'''
)

with open("app/src/main/java/com/example/ui/screens/TodayScreen.kt", "w") as f:
    f.write(content)
