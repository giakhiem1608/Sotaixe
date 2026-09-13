import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("val expenseColor = themeManager.expenseColor", "val expenseColor = themeManager.expenseColor\n    val tipColor = themeManager.tipColor")
content = content.replace("themeManager.setCardColors(bgHex, incomeHex, revHex, expHex)", "themeManager.setCardColors(bgHex, incomeHex, revHex, expHex, tipHex)")

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)
