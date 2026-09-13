import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

# Add tipColor key
content = content.replace('val EXP_COLOR_KEY = stringPreferencesKey("expense_color")',
                          'val EXP_COLOR_KEY = stringPreferencesKey("expense_color")\n        val TIP_COLOR_KEY = stringPreferencesKey("tip_color")')

# Add flow
content = content.replace("val expenseColor = dataStore.data.map { it[EXP_COLOR_KEY] ?: \"\" }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), \"\")",
                          "val expenseColor = dataStore.data.map { it[EXP_COLOR_KEY] ?: \"\" }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), \"\")\n    val tipColor = dataStore.data.map { it[TIP_COLOR_KEY] ?: \"\" }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), \"\")")

# Add to update function
content = content.replace("fun updateThemeColors(bg: String, income: String, rev: String, exp: String) {",
                          "fun updateThemeColors(bg: String, income: String, rev: String, exp: String, tip: String = \"\") {")

content = content.replace("prefs[EXP_COLOR_KEY] = exp\n            }", "prefs[EXP_COLOR_KEY] = exp\n                if (tip.isNotEmpty()) prefs[TIP_COLOR_KEY] = tip else prefs.remove(TIP_COLOR_KEY)\n            }")

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)

