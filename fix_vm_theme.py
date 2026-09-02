import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

content = content.replace('val revenueThemeColor = themeManager.revenueThemeColor\n', '')
content = content.replace('val themeManager = ThemeManager(repository.sharedPreferences)', 
'''val themeManager = ThemeManager(repository.sharedPreferences)
    val cardBgColor = themeManager.cardBgColor
    val incomeColor = themeManager.incomeColor
    val revenueColor = themeManager.revenueColor
    val expenseColor = themeManager.expenseColor''')

content = content.replace('fun updateRevenueTheme(colorHex: String) {\n        themeManager.setRevenueThemeColor(colorHex)\n    }', 
'''fun updateCardColors(bgHex: String, incomeHex: String, revHex: String, expHex: String) {
        themeManager.setCardColors(bgHex, incomeHex, revHex, expHex)
    }''')

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)
