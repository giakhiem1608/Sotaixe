import re

with open("app/src/main/java/com/example/ui/viewmodels/ThemeManager.kt", "r") as f:
    content = f.read()

# Default to Midnight Finance background #111827
content = content.replace('sharedPreferences?.getString("cardBgColor", "#16A34A") ?: "#16A34A"', 'sharedPreferences?.getString("cardBgColor", "#111827") ?: "#111827"')

content = content.replace("val expenseColor: StateFlow<String> = _expenseColor", "val expenseColor: StateFlow<String> = _expenseColor\n\n    private val _tipColor = MutableStateFlow(sharedPreferences?.getString(\"tipColor\", \"\") ?: \"\")\n    val tipColor: StateFlow<String> = _tipColor")

content = content.replace("fun setCardColors(bgHex: String, incomeHex: String, revHex: String, expHex: String) {", "fun setCardColors(bgHex: String, incomeHex: String, revHex: String, expHex: String, tipHex: String = \"\") {")

content = content.replace('putString("expenseColor", expHex)\n            apply()', 'putString("expenseColor", expHex)\n            putString("tipColor", tipHex)\n            apply()')

content = content.replace("_expenseColor.value = expHex", "_expenseColor.value = expHex\n        _tipColor.value = tipHex")

with open("app/src/main/java/com/example/ui/viewmodels/ThemeManager.kt", "w") as f:
    f.write(content)
